package dev.slne.surf.enchantment.paper.listener

import dev.slne.surf.enchantment.api.enchantment.EnchantmentManager
import org.bukkit.enchantments.Enchantment
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.enchantment.EnchantItemEvent
import org.bukkit.event.world.LootGenerateEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.EnchantmentStorageMeta

object GameplayObtainabilityListener : Listener {
    @EventHandler
    fun onLootGenerate(event: LootGenerateEvent) {
        val blockedEnchantments = blockedGameplayEnchantments()
        if (blockedEnchantments.isEmpty()) return

        event.loot.forEach { item ->
            item.removeBlockedEnchantments(blockedEnchantments)
        }
    }

    @EventHandler
    fun onEnchantItem(event: EnchantItemEvent) {
        val blockedEnchantments = blockedGameplayEnchantments()
        if (blockedEnchantments.isEmpty()) return

        event.enchantsToAdd.keys.removeAll(blockedEnchantments)
    }

    private fun blockedGameplayEnchantments() = EnchantmentManager.customEnchantments
        .filterNot { it.obtainableFromGameplay }
        .mapTo(mutableSetOf()) { it.bukkitEnchantment }

    private fun ItemStack.removeBlockedEnchantments(blockedEnchantments: Set<Enchantment>) {
        for (enchantment in enchantments.keys) {
            if (enchantment in blockedEnchantments) {
                removeEnchantment(enchantment)
            }
        }

        val storageMeta = itemMeta as? EnchantmentStorageMeta ?: return
        var changed = false
        for (enchantment in storageMeta.storedEnchants.keys) {
            if (enchantment in blockedEnchantments) {
                storageMeta.removeStoredEnchant(enchantment)
                changed = true
            }
        }

        if (changed) {
            itemMeta = storageMeta
        }
    }
}
