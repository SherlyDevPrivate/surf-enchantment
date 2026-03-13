package dev.slne.surf.enchantment.api.enchantments.replenish

import dev.slne.surf.enchantment.api.enchantment.CustomEnchantment
import dev.slne.surf.surfapi.core.api.util.requiredService

private val impl = requiredService<ReplenishEnchantment>()

interface ReplenishEnchantment : CustomEnchantment {
    companion object : ReplenishEnchantment by impl
}