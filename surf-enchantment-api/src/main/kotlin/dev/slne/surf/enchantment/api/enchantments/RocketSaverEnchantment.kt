package dev.slne.surf.enchantment.api.enchantments

import dev.slne.surf.api.core.util.requiredService
import dev.slne.surf.enchantment.api.enchantment.CustomEnchantment

private val impl = requiredService<RocketSaverEnchantment>()

interface RocketSaverEnchantment : CustomEnchantment {
    companion object : RocketSaverEnchantment by impl
}