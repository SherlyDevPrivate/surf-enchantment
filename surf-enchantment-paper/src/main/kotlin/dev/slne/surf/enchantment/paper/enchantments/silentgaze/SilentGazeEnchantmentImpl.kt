@file:Suppress("UnstableApiUsage")

package dev.slne.surf.enchantment.paper.enchantments.silentgaze

import com.google.auto.service.AutoService
import dev.slne.surf.enchantment.api.enchantment.AbstractCustomEnchantment
import dev.slne.surf.enchantment.api.enchantments.SilentGazeEnchantment
import dev.slne.surf.enchantment.api.enchantments.SilentNightEnchantment
import dev.slne.surf.enchantment.api.utils.CustomItemTypeTags
import dev.slne.surf.enchantment.paper.enchantments.silentgaze.listeners.SilentGazeListener
import dev.slne.surf.surfapi.core.api.messages.adventure.key
import dev.slne.surf.surfapi.core.api.messages.adventure.text
import dev.slne.surf.surfapi.core.api.rarity.Rarity
import io.papermc.paper.registry.data.EnchantmentRegistryEntry
import io.papermc.paper.registry.keys.tags.EnchantmentTagKeys
import org.bukkit.inventory.EquipmentSlotGroup

@AutoService(SilentGazeEnchantment::class)
class SilentGazeEnchantmentImpl : AbstractCustomEnchantment(
    key = key("surf", "silent_gaze"),
    displayName = text("Silent Gaze"),
    rarity = Rarity.EPIC,
    description = {
        line {
            darkSpacer("Ein Enderman wird dich nicht angreifen")
        }
    },
    supportedItems = CustomItemTypeTags.SILENT_GAZE_KEY.tagKey,
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
    exclusiveWith = setOf(SilentNightEnchantment.key),
    activeSlots = setOf(EquipmentSlotGroup.HEAD),
    listeners = setOf(SilentGazeListener),
), SilentGazeEnchantment