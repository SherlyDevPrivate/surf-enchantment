package dev.slne.surf.enchantment.api.enchantment

import dev.slne.surf.api.core.rarity.Rarity
import dev.slne.surf.api.paper.builder.LoreBuilder
import dev.slne.surf.enchantment.api.utils.Enchantable
import net.kyori.adventure.key.Key
import net.kyori.adventure.text.Component

data class VanillaEnchantment(
    override val key: Key,
    override val displayName: Component,
    override val description: LoreBuilder.(Int) -> Unit,
    override val rarity: Rarity,
    override val maxLevel: Int?,
) : Enchantable