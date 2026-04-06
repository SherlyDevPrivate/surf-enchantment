package dev.slne.surf.enchantment.api.enchantments.telekinesis

import dev.slne.surf.api.core.util.requiredService
import dev.slne.surf.enchantment.api.enchantment.CustomEnchantment

private val impl = requiredService<TelekinesisEnchantment>()

interface TelekinesisEnchantment : CustomEnchantment {
    companion object : TelekinesisEnchantment by impl
}