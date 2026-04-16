package dev.slne.surf.enchantment.paper.enchantments.telekinesis.listeners

import dev.slne.surf.enchantment.api.enchantments.telekinesis.PostTelekinesisItemEvent
import dev.slne.surf.enchantment.api.enchantments.telekinesis.TelekinesisEnchantment
import dev.slne.surf.enchantment.api.utils.hasCustomEnchantment
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockDropItemEvent
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.player.PlayerShearEntityEvent
import org.bukkit.inventory.ItemStack

object TelekinesisListener : Listener {

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onBlockBreak(event: BlockBreakEvent) {
        val player = event.player

        if (!player.inventory.itemInMainHand.hasCustomEnchantment<TelekinesisEnchantment>()) return

        player.giveExp(event.expToDrop, true)
        event.expToDrop = 0
    }

    @EventHandler
    fun onBlockDrop(event: BlockDropItemEvent) {
        val player = event.player
        if (!player.inventory.itemInMainHand.hasCustomEnchantment<TelekinesisEnchantment>()) return

        val dropLocation = event.block.location.clone().add(0.5, 0.5, 0.5)
        val drops = event.items.map { it.itemStack }

        addDropsToInventory(player, drops, event, dropLocation)

        event.items.clear()
    }

    @EventHandler
    fun onEntityDeath(event: EntityDeathEvent) {
        val player = event.entity.killer ?: return
        if (!player.inventory.itemInMainHand.hasCustomEnchantment<TelekinesisEnchantment>()) return

        val dropLocation = event.entity.location.clone()
        val drops = event.drops.toList()

        addDropsToInventory(player, drops, event, dropLocation)

        player.giveExp(event.droppedExp, true)
        event.droppedExp = 0
        event.drops.clear()
    }

    @EventHandler
    fun onPlayerShearEntity(event: PlayerShearEntityEvent) {
        val player = event.player
        if (!player.inventory.itemInMainHand.hasCustomEnchantment<TelekinesisEnchantment>()) return

        val dropLocation = event.entity.location.clone()
        val drops = event.drops.toList()

        addDropsToInventory(player, drops, event, dropLocation)

        event.drops = emptyList()
    }

    private fun addDropsToInventory(
        player: Player,
        drops: List<ItemStack>,
        originEvent: Event,
        dropLocation: Location
    ) {
        drops.forEach { drop ->
            val notAdded = player.inventory.addItem(drop)

            val postTelekinesisItemEvent = PostTelekinesisItemEvent(
                player = player,
                itemStack = drop,
                notAddedToInventory = notAdded.toMap(),
                originEvent = originEvent
            )
            postTelekinesisItemEvent.callEvent()

            notAdded.values.forEach { item ->
                dropLocation.world.dropItemNaturally(dropLocation, item)
            }
        }
    }
}