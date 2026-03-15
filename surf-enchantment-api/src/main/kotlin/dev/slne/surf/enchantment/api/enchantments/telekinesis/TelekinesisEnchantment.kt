package dev.slne.surf.enchantment.api.enchantments.telekinesis

import dev.slne.surf.enchantment.api.enchantment.CustomEnchantment
import dev.slne.surf.surfapi.core.api.util.requiredService

private val impl = requiredService<TelekinesisEnchantment>()

interface TelekinesisEnchantment : CustomEnchantment {
    companion object : TelekinesisEnchantment by impl
}