@file:Suppress("UnstableApiUsage")

package dev.slne.surf.enchantment.api.utils

import dev.slne.surf.enchantment.api.enchantment.CustomEnchantment
import dev.slne.surf.enchantment.api.enchantment.EnchantmentManager
import dev.slne.surf.enchantment.api.enchantment.findCustomEnchantment
import io.papermc.paper.datacomponent.DataComponentTypes
import org.bukkit.GameMode
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

fun ItemStack.hasCustomEnchantment(customEnchantment: CustomEnchantment): Boolean {
    return customEnchantment.checkItemStackHasEnchantment(this)
}

fun ItemStack.getThisEnchantmentOrNull(customEnchantment: CustomEnchantment): Pair<Int, Enchantment>? {
    return customEnchantment.getThisEnchantmentOrNull(this)
}

fun ItemStack.applyItemDamage(damage: Int, player: Player) {
    if (player.gameMode == GameMode.CREATIVE) return

    damage(damage, player)
}

fun ItemStack.calculateDurability(): Int {
    val damage = getData(DataComponentTypes.DAMAGE) ?: return 0
    val maxDamage = getData(DataComponentTypes.MAX_DAMAGE) ?: return 0

    return maxDamage - damage
}

inline fun <reified E : CustomEnchantment> ItemStack.hasCustomEnchantment(): Boolean {
    return EnchantmentManager.findCustomEnchantment<E>()?.checkItemStackHasEnchantment(this)
        ?: false
}

inline fun <reified E : CustomEnchantment> ItemStack.getThisEnchantmentOrNull(): Pair<Int, Enchantment>? {
    return EnchantmentManager.findCustomEnchantment<E>()?.getThisEnchantmentOrNull(this)
}