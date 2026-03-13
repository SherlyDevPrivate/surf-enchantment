package dev.slne.surf.enchantment.paper.enchantments.silentgaze

import com.google.auto.service.AutoService
import dev.slne.surf.enchantment.api.enchantment.AbstractCustomEnchantment
import dev.slne.surf.enchantment.api.enchantments.SilentGazeEnchantment
import dev.slne.surf.enchantment.api.enchantments.SilentNightEnchantment
import dev.slne.surf.enchantment.api.utils.EnchantmentRarity
import dev.slne.surf.enchantment.paper.enchantments.silentgaze.listeners.SilentGazeListener
import dev.slne.surf.surfapi.core.api.messages.adventure.key
import dev.slne.surf.surfapi.core.api.messages.adventure.text
import io.papermc.paper.registry.keys.tags.ItemTypeTagKeys
import org.bukkit.inventory.EquipmentSlotGroup

@AutoService(SilentGazeEnchantment::class)
class SilentGazeEnchantmentImpl : AbstractCustomEnchantment(
    key = key("surf", "silent_gaze"),
    displayName = text("Silent Gaze"),
    rarity = EnchantmentRarity.EPIC,
    description = {
        line {
            darkSpacer("Ein Enderman wird dich nicht angreifen")
        }
    },
    supportedItems = ItemTypeTagKeys.ENCHANTABLE_HEAD_ARMOR,
    exclusiveWith = setOf(SilentNightEnchantment.key),
    activeSlots = setOf(EquipmentSlotGroup.HEAD),
    listeners = setOf(SilentGazeListener),
), SilentGazeEnchantment