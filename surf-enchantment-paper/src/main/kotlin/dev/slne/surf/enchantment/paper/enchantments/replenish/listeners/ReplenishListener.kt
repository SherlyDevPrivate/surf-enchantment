package dev.slne.surf.enchantment.paper.enchantments.replenish.listeners

import dev.slne.surf.enchantment.api.enchantments.replenish.ReplenishBlockEvent
import dev.slne.surf.enchantment.api.enchantments.replenish.ReplenishEnchantment
import dev.slne.surf.enchantment.api.utils.hasThisEnchantmentActive
import dev.slne.surf.surfapi.bukkit.api.event.cancel
import dev.slne.surf.surfapi.core.api.util.object2ObjectMapOf
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.block.data.Ageable
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockDropItemEvent
import org.bukkit.inventory.ItemStack

object ReplenishListener : Listener {
    private val seedMap = object2ObjectMapOf(
        Material.WHEAT to Material.WHEAT_SEEDS,
        Material.POTATOES to Material.POTATO,
        Material.CARROTS to Material.CARROT,
        Material.BEETROOTS to Material.BEETROOT_SEEDS,
        Material.NETHER_WART to Material.NETHER_WART
    )

    @EventHandler(priority = EventPriority.LOWEST)
    fun onBlockBreak(event: BlockDropItemEvent) {
        val player = event.player
        if (!player.hasThisEnchantmentActive<ReplenishEnchantment>()) return

        val blockState = event.blockState
        val brokenBlockType = blockState.type
        val seedType = seedMap[brokenBlockType] ?: return
        val data = blockState.blockData
        if (data is Ageable && data.age < data.maximumAge) return

        val seedItemStack = ItemStack.of(seedType)

        val hasSeedInInventory = player.inventory.containsAtLeast(seedItemStack, 1)
        if (hasSeedInInventory) {
            val replenishBlockEvent = ReplenishBlockEvent(
                block = event.block,
                seed = seedItemStack,
                items = event.items,
                player = player,
            )

            if (!replenishBlockEvent.callEvent()) {
                event.cancel()
                return
            }

            event.items.clear()
            event.items.addAll(replenishBlockEvent.items)

            if (replenishBlockEvent.shouldConsumeSeed) {
                player.inventory.removeItem(seedItemStack)
            }

            event.block.type = brokenBlockType

            Particle.HAPPY_VILLAGER.builder()
                .location(event.block.location)
                .count(1)
                .spawn()
        }
    }
}