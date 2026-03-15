package dev.slne.surf.enchantment.paper.enchantments.telekinesis

import com.google.auto.service.AutoService
import dev.slne.surf.enchantment.api.enchantment.AbstractCustomEnchantment
import dev.slne.surf.enchantment.api.enchantments.TelekinesisEnchantment
import dev.slne.surf.enchantment.api.utils.CustomItemTypeTags
import dev.slne.surf.enchantment.paper.enchantments.telekinesis.listeners.TelekinesisListener
import dev.slne.surf.surfapi.core.api.messages.adventure.key
import dev.slne.surf.surfapi.core.api.messages.adventure.text
import dev.slne.surf.surfapi.core.api.rarity.Rarity
import dev.slne.surf.surfapi.core.api.util.objectSetOf
import io.papermc.paper.registry.keys.tags.EnchantmentTagKeys

@AutoService(TelekinesisEnchantment::class)
class TelekinesisEnchantmentImpl : AbstractCustomEnchantment(
    key = key("surf", "telekinesis"),
    displayName = text("Telekinesis"),
    rarity = Rarity.RARE,
    description = {
        line {
            darkSpacer("Drops und Erfahrung gehen direkt in dein Inventar")
        }
    },
    supportedItems = CustomItemTypeTags.TOOLS_AND_SWORDS_KEY.tagKey,
    tags = objectSetOf(
        EnchantmentTagKeys.IN_ENCHANTING_TABLE
    ),
    maxLevel = 1,
    listeners = objectSetOf(TelekinesisListener)
), TelekinesisEnchantment