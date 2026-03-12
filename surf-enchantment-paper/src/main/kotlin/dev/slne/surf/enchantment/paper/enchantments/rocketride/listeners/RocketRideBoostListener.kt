package dev.slne.surf.enchantment.paper.enchantments.rocketride.listeners

import com.github.benmanes.caffeine.cache.Caffeine
import dev.slne.surf.enchantment.api.enchantment.EnchantmentJob
import dev.slne.surf.enchantment.api.enchantments.RocketRideEnchantment
import dev.slne.surf.enchantment.api.utils.hasCustomEnchantment
import dev.slne.surf.enchantment.paper.enchantments.rocketride.RocketBoost
import dev.slne.surf.enchantment.paper.enchantments.rocketride.RocketRideBoostService
import dev.slne.surf.surfapi.bukkit.api.extensions.server
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.messages.adventure.playSound
import net.kyori.adventure.sound.Sound
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.entity.HappyGhast
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.entity.EntityDropItemEvent
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.meta.FireworkMeta
import org.bukkit.persistence.PersistentDataType
import java.time.OffsetDateTime
import java.util.*
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration
import org.bukkit.Sound as BukkitSound

object RocketRideBoostListener : Listener {
    private val specialHappyGhastKey = NamespacedKey("surf", "rocket-ride-happy-ghast")

    private val cooldowns = Caffeine.newBuilder()
        .expireAfterWrite(30.seconds.toJavaDuration())
        .build<UUID, OffsetDateTime>()

    private val ROCKET_PROPERTIES = mapOf(
        1 to RocketBoost(1.4, 0.5, 5),
        2 to RocketBoost(1.9, 0.7, 10),
        3 to RocketBoost(2.6, 0.9, 15)
    )

    @EventHandler
    fun onAddHarness(event: PlayerInteractEntityEvent) {
        val player = event.player
        val happyGhast = event.rightClicked as? HappyGhast ?: return

        val itemInMain = player.inventory.itemInMainHand
        if (!itemInMain.hasCustomEnchantment<RocketRideEnchantment>()) return

        happyGhast.persistentDataContainer.set(specialHappyGhastKey, PersistentDataType.BYTE, 1)
    }

    @EventHandler
    fun onRemoveHarness(event: EntityDropItemEvent) {
        val happyGhast = event.entity as? HappyGhast ?: return

        if (!happyGhast.persistentDataContainer.has(
                specialHappyGhastKey,
                PersistentDataType.BYTE
            )
        ) return
        happyGhast.persistentDataContainer.remove(specialHappyGhastKey)

        event.itemDrop.itemStack.addEnchantment(RocketRideEnchantment.bukkitEnchantment, 1)
    }

    @EventHandler
    fun onUseRocketToBoost(event: PlayerInteractEvent) {
        val player = event.player
        val happyGhast = player.vehicle as? HappyGhast ?: return
        if (!happyGhast.persistentDataContainer.has(
                specialHappyGhastKey,
                PersistentDataType.BYTE
            )
        ) return

        val item = player.inventory.itemInMainHand
        if (item.type != Material.FIREWORK_ROCKET) return

        if (happyGhast.passengers.firstOrNull() != player) {
            player.sendActionBar(buildText { error("Du hältst nicht die Zügel des Ghasts!") })
            return
        }

        if (!checkCooldown(player, happyGhast)) return

        val rocketMeta = item.itemMeta as? FireworkMeta ?: return
        val tier = rocketMeta.power.coerceIn(1, 3)
        val boost = ROCKET_PROPERTIES[tier] ?: ROCKET_PROPERTIES[1]!!

        RocketRideBoostService.startBoost(
            ghast = happyGhast,
            rider = player,
            power = 0.9 * boost.multiplier,
            upward = boost.upward,
            durationTicks = 20 + tier * 10
        )

        if (player.gameMode != GameMode.CREATIVE) {
            item.amount -= 1
        }

        applyCooldown(happyGhast, boost.cooldownSeconds)

        happyGhast.passengers.forEach { passenger ->
            passenger.sendActionBar(buildText { success("Der Happy Ghast wurde geboostet!") })
            passenger.playSound {
                type(BukkitSound.ENTITY_FIREWORK_ROCKET_BLAST)
                source(Sound.Source.NEUTRAL)
                pitch(1.0f)
            }
        }
    }

    @EventHandler
    fun onGhastDeath(event: EntityDeathEvent) {
        val happyGhast = event.entity as? HappyGhast ?: return
        if (!happyGhast.persistentDataContainer.has(
                specialHappyGhastKey,
                PersistentDataType.BYTE
            )
        ) return

        if (!RocketRideBoostService.isBoosting(happyGhast)) return
        RocketRideBoostService.stopBoost(happyGhast)

        cooldowns.invalidate(happyGhast.uniqueId)
    }

    object RocketRideBoostJob : EnchantmentJob() {
        override suspend fun tick() {
            val now = OffsetDateTime.now()

            cooldowns.asMap().forEach { (uuid, expireTime) ->
                if (expireTime.isBefore(now)) {
                    cooldowns.invalidate(uuid)

                    val entity = server.getEntity(uuid) ?: return@forEach //TODO: use context

                    entity.passengers.filterIsInstance<Player>().forEach { passenger ->
                        passenger.sendActionBar(
                            buildText {
                                success("Der Happy Ghast ist wieder fit!")
                            }
                        )
                    }
                }
            }
        }
    }

    private fun checkCooldown(player: Player, ghast: HappyGhast): Boolean {
        val expire = cooldowns.getIfPresent(ghast.uniqueId) ?: return true

        val now = OffsetDateTime.now()
        if (expire.isAfter(now)) {
            val secondsLeft = expire.toEpochSecond() - now.toEpochSecond()

            player.sendActionBar(
                buildText {
                    error("Der Ghast ist noch außer puste! Erst in")
                    appendSpace()
                    variableValue("$secondsLeft Sekunden")
                    appendSpace()
                    error("wieder fit.")
                }
            )
            return false
        }

        return true
    }

    private fun applyCooldown(ghast: HappyGhast, seconds: Long) {
        cooldowns.put(
            ghast.uniqueId,
            OffsetDateTime.now().plusSeconds(seconds)
        )
    }
}