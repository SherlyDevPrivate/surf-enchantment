package dev.slne.surf.enchantment.paper.enchantments.happyghastboost.listeners

import dev.slne.surf.enchantment.api.enchantments.HappyGhastBoostEnchantment
import dev.slne.surf.enchantment.api.utils.hasCustomEnchantment
import dev.slne.surf.enchantment.paper.enchantments.experience.ExperienceEnchantmentImpl.bukkitEnchantment
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.messages.adventure.playSound
import net.kyori.adventure.sound.Sound
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.entity.HappyGhast
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDropItemEvent
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.meta.FireworkMeta
import org.bukkit.persistence.PersistentDataType
import org.bukkit.util.Vector
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import org.bukkit.Sound as BukkitSound

object HappyGhastBoostListener : Listener {
    private val specialHappyGhastKey = NamespacedKey("surf", "special-ghast")
    private val happyGhastCooldownExpire: MutableMap<UUID, Long> = ConcurrentHashMap()

    private val ROCKET_PROPERTIES = mapOf(
        1 to Triple(1.4, 0.5, 5L),
        2 to Triple(1.9, 0.7, 10L),
        3 to Triple(2.6, 0.9, 15L)
    )

    @EventHandler
    fun onAddHarness(event: PlayerInteractEntityEvent) {
        val player = event.player
        val happyGhast = event.rightClicked as? HappyGhast ?: return

        val itemInMain = player.inventory.itemInMainHand
        if (!itemInMain.hasCustomEnchantment<HappyGhastBoostEnchantment>()) return

        happyGhast.persistentDataContainer.set(specialHappyGhastKey, PersistentDataType.BYTE, 1)
    }

    @EventHandler
    fun onEntityDropItem(event: EntityDropItemEvent) {
        val happyGhast = event.entity as? HappyGhast ?: return

        if (!happyGhast.persistentDataContainer.has(
                specialHappyGhastKey,
                PersistentDataType.BYTE
            )
        ) return
        happyGhast.persistentDataContainer.remove(specialHappyGhastKey)

        event.itemDrop.itemStack.addEnchantment(bukkitEnchantment, 1)
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
            player.sendActionBar(
                buildText {
                    error("Du hältst nicht die Zügel des Ghasts!")
                }
            )
            return
        }

        val cooldownExpire = happyGhastCooldownExpire[happyGhast.uniqueId] ?: 0L
        val now = System.currentTimeMillis()

        if (now < cooldownExpire) {
            val secondsLeft = ((cooldownExpire - now) / 1000L).toInt()

            player.sendActionBar(
                buildText {
                    error("Der Ghast kann erst wieder in ")
                    variableKey(secondsLeft)
                    error(" Sekunden geboostet werden!")
                }
            )

            return
        }

        val rocketMeta = item.itemMeta as? FireworkMeta ?: return
        val tier = rocketMeta.power.coerceIn(1, 3)
        val (powerMultiply, upward) = ROCKET_PROPERTIES[tier] ?: ROCKET_PROPERTIES[1]!!

        val look = player.location.direction.normalize()
        val basePower = 0.9
        val vx = look.x * basePower * powerMultiply
        val vy = look.y * basePower * powerMultiply + upward
        val vz = look.z * basePower * powerMultiply

        happyGhast.velocity.add(Vector(vx, vy, vz))

        if (player.gameMode != GameMode.CREATIVE) {
            item.amount -= 1
        }

        val boostCooldown = ROCKET_PROPERTIES[tier]?.third ?: (ROCKET_PROPERTIES[1]!!.third * 1000L)

        happyGhastCooldownExpire[happyGhast.uniqueId] = now + boostCooldown

        happyGhast.passengers.forEach { passanger ->
            passanger.sendActionBar(
                buildText {
                    success("Der Happy Ghast wurde geboostet!")
                }
            )

            passanger.playSound {
                type(BukkitSound.ENTITY_FIREWORK_ROCKET_BLAST)
                source(Sound.Source.NEUTRAL)
                pitch(1.0f)
            }
        }
    }
}