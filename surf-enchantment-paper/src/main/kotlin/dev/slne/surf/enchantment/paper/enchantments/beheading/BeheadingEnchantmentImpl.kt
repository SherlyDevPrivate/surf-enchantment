package dev.slne.surf.enchantment.paper.enchantments.beheading

import com.google.auto.service.AutoService
import dev.slne.surf.enchantment.api.enchantment.AbstractCustomEnchantment
import dev.slne.surf.enchantment.api.enchantments.BeheadingEnchantment
import dev.slne.surf.enchantment.paper.enchantments.beheading.listeners.BeheadingListener
import dev.slne.surf.surfapi.core.api.messages.adventure.key
import dev.slne.surf.surfapi.core.api.messages.adventure.text
import dev.slne.surf.surfapi.core.api.rarity.Rarity
import dev.slne.surf.surfapi.core.api.util.objectSetOf
import io.papermc.paper.registry.keys.EnchantmentKeys
import io.papermc.paper.registry.keys.tags.EnchantmentTagKeys
import io.papermc.paper.registry.keys.tags.ItemTypeTagKeys
import org.bukkit.inventory.EquipmentSlotGroup

@AutoService(BeheadingEnchantment::class)
class BeheadingEnchantmentImpl : AbstractCustomEnchantment(
    key = key("surf", "beheading"),
    displayName = text("Beheading"),
    rarity = Rarity.EPIC,
    description = {
        line {
            darkSpacer("Lässt den Kopf des Opfers fallen")
        }
    },
    supportedItems = setOf(ItemTypeTagKeys.ENCHANTABLE_WEAPON),
    activeSlots = setOf(EquipmentSlotGroup.MAINHAND),
    tags = objectSetOf(
        EnchantmentTagKeys.IN_ENCHANTING_TABLE,
        EnchantmentTagKeys.TREASURE
    ),
    exclusiveWith = setOf(EnchantmentKeys.LOOTING),
    maxLevel = 3,
    listeners = objectSetOf(BeheadingListener)
), BeheadingEnchantment