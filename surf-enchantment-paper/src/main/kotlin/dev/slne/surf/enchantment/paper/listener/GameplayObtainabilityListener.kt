@file:Suppress("UnstableApiUsage")

package dev.slne.surf.enchantment.paper.listener

import dev.slne.surf.enchantment.api.enchantment.EnchantmentManager
import io.papermc.paper.datacomponent.DataComponentTypes
import io.papermc.paper.datacomponent.item.ItemEnchantments
import org.bukkit.enchantments.Enchantment
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.enchantment.EnchantItemEvent
import org.bukkit.event.world.LootGenerateEvent
import org.bukkit.inventory.ItemStack

object GameplayObtainabilityListener : Listener {
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun onLootGenerate(event: LootGenerateEvent) {
        val blockedEnchantments = blockedGameplayEnchantments()
        if (blockedEnchantments.isEmpty()) return

        event.loot.forEach { item ->
            item.removeBlockedEnchantments(blockedEnchantments)
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun onEnchantItem(event: EnchantItemEvent) {
        val blockedEnchantments = blockedGameplayEnchantments()
        if (blockedEnchantments.isEmpty()) return

        event.enchantsToAdd.keys.removeAll(blockedEnchantments)
    }

    private fun blockedGameplayEnchantments() = EnchantmentManager.customEnchantments
        .filterNot { it.obtainableFromGameplay }
        .mapTo(mutableSetOf()) { it.bukkitEnchantment }

    private fun ItemStack.removeBlockedEnchantments(blockedEnchantments: Set<Enchantment>) {
        val enchants = getData(DataComponentTypes.ENCHANTMENTS)
        if (enchants != null) {
            val filtered = enchants.enchantments().filterKeys { it !in blockedEnchantments }
            if (filtered.size < enchants.enchantments().size) {
                setData(DataComponentTypes.ENCHANTMENTS, ItemEnchantments.itemEnchantments(filtered, enchants.showInTooltip()))
            }
        }

        val storedEnchants = getData(DataComponentTypes.STORED_ENCHANTMENTS)
        if (storedEnchants != null) {
            val filtered = storedEnchants.enchantments().filterKeys { it !in blockedEnchantments }
            if (filtered.size < storedEnchants.enchantments().size) {
                setData(DataComponentTypes.STORED_ENCHANTMENTS, ItemEnchantments.itemEnchantments(filtered, storedEnchants.showInTooltip()))
            }
        }
    }
}
