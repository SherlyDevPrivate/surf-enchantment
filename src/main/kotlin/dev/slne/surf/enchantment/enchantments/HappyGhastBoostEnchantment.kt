@file:Suppress("UnstableApiUsage")

package dev.slne.surf.enchantment.enchantments

import dev.slne.surf.enchantment.enchantment.CustomEnchantment
import dev.slne.surf.enchantment.utils.EnchantmentRarity
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import dev.slne.surf.surfapi.core.api.messages.adventure.key
import dev.slne.surf.surfapi.core.api.messages.adventure.playSound
import dev.slne.surf.surfapi.core.api.messages.adventure.text
import dev.slne.surf.surfapi.core.api.util.objectSetOf
import io.papermc.paper.registry.keys.tags.EnchantmentTagKeys
import io.papermc.paper.registry.keys.tags.ItemTypeTagKeys
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.entity.HappyGhast
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDropItemEvent
import org.bukkit.event.player.PlayerInteractEntityEvent
import org.bukkit.inventory.meta.FireworkMeta
import org.bukkit.persistence.PersistentDataType
import org.bukkit.util.Vector
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import net.kyori.adventure.sound.Sound as AdventureSound
import org.bukkit.Sound as BukkitSound

private val specialHappyGhastKey = NamespacedKey("surf", "special-ghast")
private val happyGhastCooldownExpire: MutableMap<UUID, Long> = ConcurrentHashMap()
private val ROCKET_PROPERTIES = mapOf(
    1 to Triple(1.4, 0.5, 5L),
    2 to Triple(1.9, 0.7, 10L),
    3 to Triple(2.6, 0.9, 15L)
)

object HappyGhastBoostEnchantment : CustomEnchantment(
    key("surf", "happy_ghast"),
    text("Rocket Ride"),
    EnchantmentRarity.EPIC,
    description = {
        line {
            darkSpacer("Boostet den Happy Ghast, wenn du auf ihm eine Rakete zündest.")
        }
    },
    supportedItems = ItemTypeTagKeys.HARNESSES,
    tags = objectSetOf(
        EnchantmentTagKeys.IN_ENCHANTING_TABLE,
        EnchantmentTagKeys.TREASURE
    ),
    listeners = objectSetOf(Handler)
) {
    object Handler : Listener {

        @EventHandler
        fun onAddHarness(event: PlayerInteractEntityEvent) {
            val player = event.player
            val happyGhast = event.rightClicked as? HappyGhast ?: return

            val itemInMain = player.inventory.itemInMainHand
            if (!itemInMain.hasThisEnchantment()) return

            happyGhast.persistentDataContainer.set(specialHappyGhastKey, PersistentDataType.BYTE, 1)
        }

        @EventHandler
        fun onEntityDropItem(event: EntityDropItemEvent) {
            val happyGhast = event.entity as? HappyGhast ?: return

            if (!happyGhast.persistentDataContainer.has(specialHappyGhastKey, PersistentDataType.BYTE)) return
            happyGhast.persistentDataContainer.remove(specialHappyGhastKey)

            event.itemDrop.itemStack.addEnchantment(bukkitEnchantment, 1)
        }

        @EventHandler
        fun onUseRocketToBoost(event: PlayerInteractEntityEvent) {
            val player = event.player
            val happyGhast = event.rightClicked as? HappyGhast ?: return
            if (!happyGhast.persistentDataContainer.has(specialHappyGhastKey, PersistentDataType.BYTE)) return

            val item = player.inventory.itemInMainHand
            if (item.type != Material.FIREWORK_ROCKET) return

            val happyGhastVehicle = happyGhast.vehicle ?: return
            if (happyGhastVehicle.passengers.firstOrNull() !== player) {
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
            happyGhast.velocity = Vector(vx, vy, vz)

            if (player.gameMode != GameMode.CREATIVE) {
                item.amount = item.amount - 1
            }

            var boostCooldown = ROCKET_PROPERTIES[tier]?.third ?: (ROCKET_PROPERTIES[1]!!.third * 1000L)
            happyGhastCooldownExpire.put(happyGhast.uniqueId, now + boostCooldown)

            player.sendActionBar(
                buildText {
                    success("Der Happy Ghast wurde geboostet!")
                }
            )

            player.playSound {
                type(BukkitSound.ENTITY_FIREWORK_ROCKET_BLAST)
                source(AdventureSound.Source.NEUTRAL)
                pitch(1.0f)
            }
        }
    }
}