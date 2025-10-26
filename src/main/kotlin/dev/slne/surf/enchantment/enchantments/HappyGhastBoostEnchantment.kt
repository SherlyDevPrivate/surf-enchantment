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
import net.kyori.adventure.sound.Sound as AdventureSound
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
import org.bukkit.Sound as BukkitSound


private val entityKey = NamespacedKey("surf", "special-ghast")

private val ROCKET_PROPERTIES = mapOf(
    1 to Triple(1.4, 0.5, 5),
    2 to Triple(1.9, 0.7, 10),
    3 to Triple(2.6, 0.9, 15)
)

object HappyGhastBoostEnchantment : CustomEnchantment(
    key("surf", "happy-ghast"),
    text("Happy Ghast Boost"),
    EnchantmentRarity.EPIC,
    description = {
        line {
            darkSpacer("Boostet den Ghast wenn du auf ihm eine Rakete zündest.")
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

        @EventHandler()
        fun onAddHarness(event: PlayerInteractEntityEvent) {
            val player = event.player
            val entity = event.rightClicked

            if (entity !is HappyGhast) return

            val main = player.inventory.itemInMainHand.hasThisEnchantment()
            if (!main) return

            entity.persistentDataContainer.set(entityKey, PersistentDataType.BYTE, 1)

            player.sendActionBar (
                buildText {
                    success("Das Geschirr wurde dem Ghast angelegt!")
                }
            )
        }

        @EventHandler()
        fun onEntityDropItem(event: EntityDropItemEvent) {
            val entity = event.entity

            if (entity !is HappyGhast) return

            if (!entity.persistentDataContainer.has(entityKey, PersistentDataType.BYTE)) return

            entity.persistentDataContainer.remove(entityKey)

            val drop = event.itemDrop

            // drop harness with enchantment
        }

        @EventHandler
        fun onUseRocketToBoost(event: PlayerInteractEntityEvent) {
            val player = event.player
            val entity = event.rightClicked
            val happyGhast = entity as? HappyGhast ?: return
            if (!happyGhast.persistentDataContainer.has(entityKey, PersistentDataType.BYTE)) return

            val item = player.inventory.itemInMainHand
            if (item.type != Material.FIREWORK_ROCKET) return

            val happyGhastVehicle = happyGhast.vehicle ?: return
            if (happyGhastVehicle.passengers.firstOrNull() !== player) {
                player.sendActionBar (
                    buildText {
                        error("Du hältst nicht die Zügel des Ghasts!")
                    }
                )
                return
            }

            val cooldownTicks = player.getCooldown(Material.FIREWORK_ROCKET)
            if (cooldownTicks > 0) {
                val secondsLeft = cooldownTicks / 20
                player.sendActionBar(
                    buildText {
                        error("Du kannst den Ghast erst wieder in ")
                        variableKey(secondsLeft)
                        error(" Sekunden boosten!")
                    }
                )
                return
            }

            val rocketMeta = item.itemMeta as? FireworkMeta ?: return
            val tier = rocketMeta.power.coerceIn(1, 3)
            val (powerMultiply, upward, cooldownSeconds) = ROCKET_PROPERTIES[tier] ?: ROCKET_PROPERTIES[1]!!

            val look = player.location.direction.normalize()
            val basePower = 0.9
            val vx = look.x * basePower * powerMultiply
            val vy = look.y * basePower * powerMultiply + upward
            val vz = look.z * basePower * powerMultiply
            happyGhast.velocity = Vector(vx, vy, vz)

            if (player.gameMode != GameMode.CREATIVE) {
                item.amount = item.amount - 1
            }
            player.setCooldown(Material.FIREWORK_ROCKET, cooldownSeconds * 20)

            player.sendActionBar(
                buildText {
                    success("Der Ghast wurde geboostet!")
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