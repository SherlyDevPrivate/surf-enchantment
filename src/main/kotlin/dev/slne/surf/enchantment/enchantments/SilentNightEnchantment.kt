@file:Suppress("UnstableApiUsage")

package dev.slne.surf.enchantment.enchantments

import dev.slne.surf.enchantment.enchantment.CustomEnchantment
import dev.slne.surf.enchantment.utils.CustomItemTypeTags
import dev.slne.surf.surfapi.core.api.messages.adventure.key
import dev.slne.surf.surfapi.core.api.util.objectSetOf
import io.papermc.paper.registry.keys.EnchantmentKeys
import io.papermc.paper.registry.keys.tags.EnchantmentTagKeys
import org.bukkit.event.Listener
import org.bukkit.inventory.ItemRarity

object SilentNightEnchantment : CustomEnchantment(
    key = key("surf", "silent_night"),
    displayName = { info("Silent Night") },
    rarity = ItemRarity.RARE,
    description = {
        line {
            darkSpacer("Test Beschreibung Night")
        }
    },
    supportedItems = CustomItemTypeTags.TOOLS_KEY.tagKey,
    exclusiveWith = objectSetOf(EnchantmentKeys.MENDING),
    tags = objectSetOf(EnchantmentTagKeys.CURSE),
    listeners = objectSetOf(Handler)
) {
    object Handler : Listener {

    }
}