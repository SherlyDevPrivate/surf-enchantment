package dev.slne.surf.enchantment.paper.enchantments.happyghastboost

import dev.slne.surf.enchantment.api.enchantment.AbstractCustomEnchantment
import dev.slne.surf.enchantment.api.enchantments.HappyGhastBoostEnchantment
import dev.slne.surf.enchantment.api.utils.EnchantmentRarity
import dev.slne.surf.enchantment.paper.enchantments.happyghastboost.listeners.HappyGhastBoostListener
import dev.slne.surf.surfapi.core.api.messages.adventure.key
import dev.slne.surf.surfapi.core.api.messages.adventure.text
import dev.slne.surf.surfapi.core.api.util.objectSetOf
import io.papermc.paper.registry.keys.tags.EnchantmentTagKeys
import io.papermc.paper.registry.keys.tags.ItemTypeTagKeys

object HappyGhastBoostEnchantmentImpl : AbstractCustomEnchantment(
    key = key("surf", "happy_ghast"),
    displayName = text("Rocket Ride"),
    rarity = EnchantmentRarity.EPIC,
    description = {
        line {
            darkSpacer("Boostet den Happy Ghast, wenn du auf ihm eine Rakete zündest.")
        }
    },
    supportedItems = ItemTypeTagKeys.HARNESSES,
    tags = objectSetOf(
        EnchantmentTagKeys.IN_ENCHANTING_TABLE,
        EnchantmentTagKeys.TREASURE
    ),
    listeners = objectSetOf(HappyGhastBoostListener)
), HappyGhastBoostEnchantment