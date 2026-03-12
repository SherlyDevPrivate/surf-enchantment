package dev.slne.surf.enchantment.api.enchantment

import dev.slne.surf.enchantment.api.utils.Enchantable
import dev.slne.surf.enchantment.api.utils.EnchantmentRarity
import dev.slne.surf.surfapi.bukkit.api.builder.LoreBuilder
import net.kyori.adventure.key.Key
import net.kyori.adventure.text.Component

data class VanillaEnchantment(
    override val key: Key,
    override val displayName: Component,
    override val description: LoreBuilder.(Int) -> Unit,
    override val rarity: EnchantmentRarity,
    override val maxLevel: Int?,
) : Enchantable