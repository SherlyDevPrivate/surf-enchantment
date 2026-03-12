package dev.slne.surf.enchantment.api.utils

import dev.slne.surf.enchantment.api.enchantment.CustomEnchantment
import dev.slne.surf.enchantment.api.enchantment.EnchantmentManager
import dev.slne.surf.enchantment.api.enchantment.findCustomEnchantment
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemStack

fun ItemStack.hasCustomEnchantment(customEnchantment: CustomEnchantment): Boolean {
    return customEnchantment.checkItemStackHasEnchantment(this)
}

fun ItemStack.getThisEnchantmentOrNull(customEnchantment: CustomEnchantment): Pair<Int, Enchantment>? {
    return customEnchantment.getThisEnchantmentOrNull(this)
}

inline fun <reified E : CustomEnchantment> ItemStack.hasCustomEnchantment(): Boolean {
    return EnchantmentManager.findCustomEnchantment<E>()?.checkItemStackHasEnchantment(this)
        ?: false
}

inline fun <reified E : CustomEnchantment> ItemStack.getThisEnchantmentOrNull(): Pair<Int, Enchantment>? {
    return EnchantmentManager.findCustomEnchantment<E>()?.getThisEnchantmentOrNull(this)
}