package dev.slne.surf.enchantment.utils

import dev.slne.surf.surfapi.bukkit.api.builder.LoreBuilder
import dev.slne.surf.surfapi.core.api.messages.builder.SurfComponentBuilder
import io.papermc.paper.registry.keys.EnchantmentKeys
import net.kyori.adventure.key.Key
import net.kyori.adventure.text.Component
import org.bukkit.inventory.ItemRarity

enum class VanillaEnchantment(
    override val key: Key,
    displayName: SurfComponentBuilder.() -> Unit,
    override val description: LoreBuilder.(Int) -> Unit,
    override val rarity: ItemRarity = ItemRarity.COMMON,
) : Enchantable {
    UNBREAKING(
        EnchantmentKeys.UNBREAKING,
        displayName = { spacer("Unbreaking") },
        description = { level ->
            line {
                darkSpacer("Erhöht die Haltbarkeit von Gegenständen um $level%")
            }
        },
    );

    override val displayName: Component = SurfComponentBuilder(displayName)

    companion object {
        fun getByKey(key: Key) = entries.find { it.key.asString() == key.asString() }
    }
}