package dev.slne.surf.enchantment.utils

import dev.slne.surf.surfapi.bukkit.api.builder.LoreBuilder
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import io.papermc.paper.registry.keys.EnchantmentKeys
import net.kyori.adventure.key.Key
import net.kyori.adventure.text.Component
import org.bukkit.inventory.ItemRarity

enum class VanillaEnchantment(
    override val key: Key,
    override val displayName: Component,
    override val description: LoreBuilder.(Int) -> Unit,
    override val rarity: ItemRarity
) : Enchantable {
    UNBREAKING(
        EnchantmentKeys.UNBREAKING,
        displayName = buildText { spacer("Unbreaking") },
        description = { level ->
            line {
                darkSpacer("Erhöht die Haltbarkeit von Gegenständen um $level%")
            }
        },
        rarity = ItemRarity.COMMON,
    );

    companion object {
        fun getByKey(key: Key) = entries.find { it.key.asString() == key.asString() }
    }
}