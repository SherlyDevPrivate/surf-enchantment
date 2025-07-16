@file:Suppress("UnstableApiUsage")

package dev.slne.surf.enchantment.enchantments

import dev.slne.surf.enchantment.enchantment.CustomEnchantment
import dev.slne.surf.enchantment.utils.CustomItemTypeTags
import dev.slne.surf.enchantment.utils.EnchantmentRarity
import dev.slne.surf.surfapi.core.api.messages.adventure.key
import dev.slne.surf.surfapi.core.api.messages.adventure.text
import dev.slne.surf.surfapi.core.api.util.objectSetOf
import io.papermc.paper.registry.keys.tags.EnchantmentTagKeys
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockDropItemEvent
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.inventory.ItemStack

object TelekinesisEnchantment : CustomEnchantment(
    key("surf", "telekinesis"),
    text("Telekinesis"),
    EnchantmentRarity.RARE,
    description = {
        line {
            darkSpacer("Drops und Erfahrung gehen direkt in dein Inventar")
        }
    },
    supportedItems = CustomItemTypeTags.TOOLS_AND_SWORDS_KEY.tagKey,
    tags = objectSetOf(
        EnchantmentTagKeys.IN_ENCHANTING_TABLE
    ),
    listeners = objectSetOf(
        TelekinesisListener
    )
) {
    object TelekinesisListener : Listener {

        @EventHandler
        fun onBlockBreak(event: BlockBreakEvent) {
            val player = event.player

            if (!checkItemStackHasEnchantment(player.inventory.itemInMainHand)) return
            player.giveExp(event.expToDrop, true)
        }

        @EventHandler
        fun onBlockBreak(event: BlockDropItemEvent) {
            val player = event.player

            if (!checkItemStackHasEnchantment(player.inventory.itemInMainHand)) return
            addDropsToInventory(player, event.items.map { it.itemStack })

            event.items.clear()
        }

        @EventHandler
        fun onEntityDeath(event: EntityDeathEvent) {
            val player = event.entity.killer ?: return

            if (!checkItemStackHasEnchantment(player.inventory.itemInMainHand)) return
            addDropsToInventory(player, event.drops)
            player.giveExp(event.droppedExp, true)

            event.droppedExp = 0
            event.drops.clear()
        }

        private fun addDropsToInventory(player: Player, drops: List<ItemStack>) {
            drops.forEach { drop ->
                val notAdded = player.inventory.addItem(drop)

                notAdded.forEach { _, item ->
                    player.world.dropItem(player.location, item)
                }
            }
        }

    }
}