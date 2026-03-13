package dev.slne.surf.enchantment.paper.enchantments.telekinesis.listeners

import dev.slne.surf.enchantment.api.enchantments.telekinesis.PostTelekinesisItemEvent
import dev.slne.surf.enchantment.api.enchantments.telekinesis.TelekinesisEnchantment
import dev.slne.surf.enchantment.api.utils.hasCustomEnchantment
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockDropItemEvent
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.player.PlayerShearEntityEvent
import org.bukkit.inventory.ItemStack

object TelekinesisListener : Listener {
    @EventHandler
    fun onBlockBreak(event: BlockBreakEvent) {
        val player = event.player

        if (!player.inventory.itemInMainHand.hasCustomEnchantment<TelekinesisEnchantment>()) return
        player.giveExp(event.expToDrop, true)
    }

    @EventHandler
    fun onBlockBreak(event: BlockDropItemEvent) {
        val player = event.player

        if (!player.inventory.itemInMainHand.hasCustomEnchantment<TelekinesisEnchantment>()) return
        addDropsToInventory(player, event.items.map { it.itemStack }, event)

        event.items.clear()
    }

    @EventHandler
    fun onEntityDeath(event: EntityDeathEvent) {
        val player = event.entity.killer ?: return

        if (!player.inventory.itemInMainHand.hasCustomEnchantment<TelekinesisEnchantment>()) return
        addDropsToInventory(player, event.drops, event)
        player.giveExp(event.droppedExp, true)

        event.droppedExp = 0
        event.drops.clear()
    }

    @EventHandler
    fun onPlayerShearEntity(event: PlayerShearEntityEvent) {
        val player = event.player

        if (!player.inventory.itemInMainHand.hasCustomEnchantment<TelekinesisEnchantment>()) return
        addDropsToInventory(player, event.drops, event)

        event.drops.clear()
    }

    private fun addDropsToInventory(player: Player, drops: List<ItemStack>, originEvent: Event) {
        drops.forEach { drop ->
            val notAdded = player.inventory.addItem(drop)

            val postTelekinesisItemEvent = PostTelekinesisItemEvent(
                player = player,
                itemStack = drop,
                notAddedToInventory = notAdded.toMap(),
                originEvent = originEvent
            )

            notAdded.clear()
            notAdded.putAll(postTelekinesisItemEvent.notAddedToInventory)

            notAdded.forEach { (_, item) ->
                player.world.dropItem(player.location, item)
            }
        }
    }
}