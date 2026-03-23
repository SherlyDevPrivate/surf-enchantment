package dev.slne.surf.enchantment.paper.enchantments.silentnight

import com.google.auto.service.AutoService
import dev.slne.surf.enchantment.api.enchantment.AbstractCustomEnchantment
import dev.slne.surf.enchantment.api.enchantments.SilentNightEnchantment
import dev.slne.surf.enchantment.paper.enchantments.silentnight.listeners.SilentNightListener
import dev.slne.surf.surfapi.core.api.messages.adventure.key
import dev.slne.surf.surfapi.core.api.messages.adventure.text
import dev.slne.surf.surfapi.core.api.rarity.Rarity
import io.papermc.paper.registry.keys.EnchantmentKeys
import io.papermc.paper.registry.keys.tags.EnchantmentTagKeys
import io.papermc.paper.registry.keys.tags.ItemTypeTagKeys

@AutoService(SilentNightEnchantment::class)
class SilentNightEnchantmentImpl : AbstractCustomEnchantment(
    key = key("surf", "silent_night"),
    displayName = text("Silent Night"),
    rarity = Rarity.LEGENDARY,
    description = {
        line {
            darkSpacer("Phantome werden dich nicht mehr angreifen")
        }
    },
    supportedItems = ItemTypeTagKeys.ENCHANTABLE_HEAD_ARMOR,
    tags = setOf(
        EnchantmentTagKeys.IN_ENCHANTING_TABLE,
        EnchantmentTagKeys.TREASURE
    ),
    exclusiveWith = setOf(EnchantmentKeys.LOOTING),
    maxLevel = 1,
    listeners = setOf(SilentNightListener)
), SilentNightEnchantment