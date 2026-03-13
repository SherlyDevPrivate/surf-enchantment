package dev.slne.surf.enchantment.api.enchantments

import dev.slne.surf.enchantment.api.enchantment.CustomEnchantment
import dev.slne.surf.surfapi.core.api.util.requiredService

private val impl = requiredService<HoleDiggerEnchantment>()

interface HoleDiggerEnchantment : CustomEnchantment {
    companion object : HoleDiggerEnchantment by impl
}