package dev.slne.surf.enchantment.api.enchantments.replenish

import dev.slne.surf.api.core.util.requiredService
import dev.slne.surf.enchantment.api.enchantment.CustomEnchantment

private val impl = requiredService<ReplenishEnchantment>()

interface ReplenishEnchantment : CustomEnchantment {
    companion object : ReplenishEnchantment by impl
}