package dev.slne.surf.enchantment.utils

import dev.slne.surf.surfapi.bukkit.api.builder.LoreBuilder
import net.kyori.adventure.key.Key
import net.kyori.adventure.text.Component
import org.bukkit.inventory.ItemRarity

interface Enchantable {

    val key: Key
    val displayName: Component
    val description: LoreBuilder.(Int) -> Unit
    val rarity: ItemRarity

    fun buildLore(level: Int) = LoreBuilder().apply {
        line {
            append(displayName)
        }
        description(level)
    }.build()

}