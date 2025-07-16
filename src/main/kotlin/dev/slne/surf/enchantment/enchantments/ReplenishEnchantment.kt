@file:Suppress("UnstableApiUsage")

package dev.slne.surf.enchantment.enchantments

import dev.slne.surf.enchantment.enchantment.CustomEnchantment
import dev.slne.surf.enchantment.utils.CustomItemTypeTags
import dev.slne.surf.enchantment.utils.EnchantmentRarity
import dev.slne.surf.surfapi.core.api.messages.adventure.key
import dev.slne.surf.surfapi.core.api.messages.adventure.text
import dev.slne.surf.surfapi.core.api.util.object2ObjectMapOf
import dev.slne.surf.surfapi.core.api.util.objectSetOf
import io.papermc.paper.registry.keys.tags.EnchantmentTagKeys
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockDropItemEvent
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
    supportedItems = CustomItemTypeTags.TOOLS_KEY.tagKey,
    tags = objectSetOf(
        EnchantmentTagKeys.IN_ENCHANTING_TABLE
    ),
    listeners = objectSetOf(
        ReplenishListener
    )
) {
    object ReplenishListener : Listener {
        private val seedMap = object2ObjectMapOf(
            Material.WHEAT to Material.WHEAT_SEEDS,
            Material.POTATO to Material.POTATO,
            Material.CARROT to Material.CARROT,
            Material.BEETROOT to Material.BEETROOT_SEEDS,
        )

        @EventHandler(priority = EventPriority.LOWEST)
        fun onBlockBreak(event: BlockDropItemEvent) {
            val player = event.player

            if (!checkItemStackHasEnchantment(player.inventory.itemInMainHand)) return

            val brokenBlockType = event.block.type
            val seedType = seedMap[brokenBlockType] ?: return
            val seedItemStack = ItemStack(seedType, 1)

            val hasSeedInInventory = player.inventory.containsAtLeast(seedItemStack, 1)
            if (hasSeedInInventory) {
                player.inventory.removeItem(seedItemStack)
                event.block.type = seedType

                Particle.HAPPY_VILLAGER.builder()
                    .location(event.block.location)
                    .count(1)
                    .spawn()
            }
        }

    }
}