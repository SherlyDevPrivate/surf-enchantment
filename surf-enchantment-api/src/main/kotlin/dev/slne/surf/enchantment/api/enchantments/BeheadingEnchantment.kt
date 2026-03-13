package dev.slne.surf.enchantment.api.enchantments

import dev.slne.surf.enchantment.api.enchantment.CustomEnchantment
import dev.slne.surf.surfapi.core.api.util.requiredService

private val impl = requiredService<BeheadingEnchantment>()

interface BeheadingEnchantment : CustomEnchantment {
    companion object : BeheadingEnchantment by impl
}