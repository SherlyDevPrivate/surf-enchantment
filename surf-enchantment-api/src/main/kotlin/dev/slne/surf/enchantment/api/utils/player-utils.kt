package dev.slne.surf.enchantment.api.utils

import dev.slne.surf.enchantment.api.enchantment.CustomEnchantment
import dev.slne.surf.enchantment.api.enchantment.EnchantmentManager
import dev.slne.surf.enchantment.api.enchantment.findCustomEnchantment
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player

fun Player.hasThisEnchantmentActive(customEnchantment: CustomEnchantment): Boolean {
    return customEnchantment.hasEnchantmentActive(this)
}

fun Player.getThisActiveEnchantmentOrNull(customEnchantment: CustomEnchantment): Pair<Int, Enchantment>? {
    return customEnchantment.getThisActiveEnchantmentOrNull(this)
}

inline fun <reified E : CustomEnchantment> Player.hasThisEnchantmentActive(): Boolean {
    return EnchantmentManager.findCustomEnchantment<E>()?.hasEnchantmentActive(this) ?: false
}

inline fun <reified E : CustomEnchantment> Player.getThisActiveEnchantmentOrNull(): Pair<Int, Enchantment>? {
    return EnchantmentManager.findCustomEnchantment<E>()?.getThisActiveEnchantmentOrNull(this)
}