@file:Suppress("UnstableApiUsage")

package dev.slne.surf.enchantment.enchantments

import dev.slne.surf.enchantment.enchantment.CustomEnchantment
import dev.slne.surf.enchantment.utils.EnchantmentRarity
import dev.slne.surf.surfapi.core.api.messages.adventure.key
import dev.slne.surf.surfapi.core.api.messages.adventure.text
import dev.slne.surf.surfapi.core.api.util.object2ObjectMapOf
import io.papermc.paper.registry.keys.tags.EnchantmentTagKeys
import io.papermc.paper.registry.keys.tags.ItemTypeTagKeys
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.block.data.Ageable
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockDropItemEvent
import org.bukkit.inventory.EquipmentSlotGroup
import org.bukkit.inventory.ItemStack

object ReplenishEnchantment : CustomEnchantment(
    key("surf", "replenish"),
    text("Replenish"),
    EnchantmentRarity.EPIC,
    description = {
        line {
            darkSpacer("Nutzpflanzen werden nach dem Ernten automatisch nachgepflanzt")
        }
    },
    supportedItems = ItemTypeTagKeys.HOES,
    activeSlots = setOf(EquipmentSlotGroup.MAINHAND),
    tags = setOf(
        EnchantmentTagKeys.IN_ENCHANTING_TABLE, EnchantmentTagKeys.TREASURE
    ),
    maxLevel = 1,
    listeners = setOf(Handler)
) {
    object Handler : Listener {
        private val seedMap = object2ObjectMapOf(
            Material.WHEAT to Material.WHEAT_SEEDS,
            Material.POTATOES to Material.POTATO,
            Material.CARROTS to Material.CARROT,
            Material.BEETROOTS to Material.BEETROOT_SEEDS,
            Material.NETHER_WART to Material.NETHER_WART,
            Material.TORCHFLOWER to Material.TORCHFLOWER_CROP,
        )

        @EventHandler(priority = EventPriority.LOWEST)
        fun onBlockBreak(event: BlockDropItemEvent) {
            val player = event.player
            if (!player.hasThisEnchantmentActive()) return

            val blockState = event.blockState
            val brokenBlockType = blockState.type
            val seedType = seedMap[brokenBlockType] ?: return
            val data = blockState.blockData
            if (data is Ageable && data.age < data.maximumAge) return

            val seedItemStack = ItemStack.of(seedType)

            val hasSeedInInventory = player.inventory.containsAtLeast(seedItemStack, 1)
            if (hasSeedInInventory) {
                player.inventory.removeItem(seedItemStack)
                event.block.type = brokenBlockType

                Particle.HAPPY_VILLAGER.builder()
                    .location(event.block.location)
                    .count(1)
                    .spawn()
            }
        }
    }
}