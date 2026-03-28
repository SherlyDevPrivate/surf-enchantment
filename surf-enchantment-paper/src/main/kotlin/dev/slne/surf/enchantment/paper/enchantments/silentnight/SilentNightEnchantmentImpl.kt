@file:Suppress("UnstableApiUsage")

package dev.slne.surf.enchantment.paper.enchantments.silentnight

import com.google.auto.service.AutoService
import dev.slne.surf.enchantment.api.enchantment.AbstractCustomEnchantment
import dev.slne.surf.enchantment.api.enchantments.SilentNightEnchantment
import dev.slne.surf.enchantment.paper.enchantments.silentnight.listeners.SilentNightListener
import dev.slne.surf.surfapi.core.api.messages.adventure.key
import dev.slne.surf.surfapi.core.api.messages.adventure.text
import dev.slne.surf.surfapi.core.api.rarity.Rarity
import io.papermc.paper.registry.data.EnchantmentRegistryEntry
import io.papermc.paper.registry.keys.EnchantmentKeys
import io.papermc.paper.registry.keys.tags.EnchantmentTagKeys
import io.papermc.paper.registry.keys.tags.ItemTypeTagKeys

@AutoService(SilentNightEnchantment::class)
class SilentNightEnchantmentImpl : AbstractCustomEnchantment(
    key = key("surf", "silent_night"),
    displayName = text("Silent Night"),
    rarity = Rarity.EPIC,
    description = {
        line {
            darkSpacer("Phantome werden dich nicht mehr angreifen")
        }
    },
    supportedItems = ItemTypeTagKeys.ENCHANTABLE_HEAD_ARMOR,
    weight = 2,
    minimumCost = EnchantmentRegistryEntry.EnchantmentCost.of(
        15,
        9
    ),
    maximumCost = EnchantmentRegistryEntry.EnchantmentCost.of(
        65,
        9
    ),
    tags = setOf(
        EnchantmentTagKeys.IN_ENCHANTING_TABLE,
        EnchantmentTagKeys.ON_RANDOM_LOOT,
        EnchantmentTagKeys.TREASURE
    ),
    exclusiveWith = setOf(EnchantmentKeys.LOOTING),
    maxLevel = 1,
    listeners = setOf(SilentNightListener)
), SilentNightEnchantment