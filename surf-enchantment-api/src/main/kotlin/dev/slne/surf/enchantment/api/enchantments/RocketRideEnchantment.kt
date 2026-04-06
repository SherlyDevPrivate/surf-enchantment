package dev.slne.surf.enchantment.api.enchantments

import dev.slne.surf.api.core.util.requiredService
import dev.slne.surf.enchantment.api.enchantment.CustomEnchantment

private val impl = requiredService<RocketRideEnchantment>()

interface RocketRideEnchantment : CustomEnchantment {
    companion object : RocketRideEnchantment by impl
}