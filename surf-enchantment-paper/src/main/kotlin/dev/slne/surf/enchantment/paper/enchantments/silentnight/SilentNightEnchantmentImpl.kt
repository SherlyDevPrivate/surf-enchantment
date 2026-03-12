package dev.slne.surf.enchantment.paper.enchantments.silentnight

import com.google.auto.service.AutoService
import dev.slne.surf.enchantment.api.enchantment.AbstractCustomEnchantment
import dev.slne.surf.enchantment.api.enchantments.SilentNightEnchantment
import dev.slne.surf.enchantment.api.utils.EnchantmentRarity
import dev.slne.surf.enchantment.paper.enchantments.silentnight.listeners.SilentNightListener
import dev.slne.surf.surfapi.core.api.messages.adventure.key
import dev.slne.surf.surfapi.core.api.messages.adventure.text
import io.papermc.paper.registry.keys.EnchantmentKeys
import io.papermc.paper.registry.keys.tags.EnchantmentTagKeys
import io.papermc.paper.registry.keys.tags.ItemTypeTagKeys

@AutoService(SilentNightEnchantment::class)
class SilentNightEnchantmentImpl : AbstractCustomEnchantment(
    key = key("surf", "silent_night"),
    displayName = text("Silent Night"),
    rarity = EnchantmentRarity.LEGENDARY,
    description = {
        line {
            darkSpacer("Lässt den Kopf des Opfers fallen")
        }
    },
    supportedItems = ItemTypeTagKeys.ENCHANTABLE_HEAD_ARMOR,
    tags = setOf(
        EnchantmentTagKeys.IN_ENCHANTING_TABLE,
        EnchantmentTagKeys.TREASURE
    ),
    exclusiveWith = setOf(EnchantmentKeys.LOOTING),
    maxLevel = 3,
    listeners = setOf(SilentNightListener)
), SilentNightEnchantment