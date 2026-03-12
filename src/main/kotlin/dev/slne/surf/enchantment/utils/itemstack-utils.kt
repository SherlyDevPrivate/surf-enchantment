@file:Suppress("UnstableApiUsage")

package dev.slne.surf.enchantment.utils

import io.papermc.paper.datacomponent.DataComponentTypes
import org.bukkit.inventory.ItemStack

fun ItemStack.calculateDurability(): Int {
    val damage = getData(DataComponentTypes.DAMAGE) ?: return 0
    val maxDamage = getData(DataComponentTypes.MAX_DAMAGE) ?: return 0

    return maxDamage - damage
}