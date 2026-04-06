package dev.slne.surf.enchantment.api.enchantments

import dev.slne.surf.api.core.util.requiredService
import dev.slne.surf.enchantment.api.enchantment.CustomEnchantment

private val impl = requiredService<ExperienceEnchantment>()

interface ExperienceEnchantment : CustomEnchantment {
    companion object : ExperienceEnchantment by impl
}