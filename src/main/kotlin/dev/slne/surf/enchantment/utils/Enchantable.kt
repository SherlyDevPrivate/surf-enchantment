package dev.slne.surf.enchantment.utils

import dev.slne.surf.surfapi.bukkit.api.builder.LoreBuilder
import net.kyori.adventure.key.Key
import net.kyori.adventure.text.Component
import org.jetbrains.annotations.Range

interface Enchantable {

    val key: Key
    val displayName: Component
    val description: LoreBuilder.(Int) -> Unit
    val rarity: EnchantmentRarity
    val maxLevel: @Range(from = 1, to = 255) Int?

    fun buildLore(level: Int) = LoreBuilder().apply {
        line {
            append(displayName.color(rarity.color))
            appendSpace()
            append(Component.text(level, rarity.color))
        }
        description(level)
    }.build()

}