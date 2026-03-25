package dev.slne.surf.enchantment.paper.enchantments.experience

import com.google.auto.service.AutoService
import dev.slne.surf.enchantment.api.enchantment.AbstractCustomEnchantment
import dev.slne.surf.enchantment.api.enchantments.ExperienceEnchantment
import dev.slne.surf.enchantment.api.utils.CustomItemTypeTags
import dev.slne.surf.enchantment.paper.enchantments.experience.listeners.ExperienceListener
import dev.slne.surf.surfapi.core.api.messages.adventure.key
import dev.slne.surf.surfapi.core.api.messages.adventure.text
import dev.slne.surf.surfapi.core.api.rarity.Rarity
import io.papermc.paper.registry.keys.EnchantmentKeys
import io.papermc.paper.registry.keys.tags.EnchantmentTagKeys
import org.bukkit.inventory.EquipmentSlotGroup

@AutoService(ExperienceEnchantment::class)
class ExperienceEnchantmentImpl : AbstractCustomEnchantment(
    key = key("surf", "experience"),
    displayName = text("Experience"),
    rarity = Rarity.LEGENDARY,
    description = {
        line {
            darkSpacer("Droppt dir mehr Erfahrung")
        }
    },
    supportedItems = setOf(CustomItemTypeTags.TOOLS_AND_WEAPONS_KEY.tagKey),
    tags = setOf(
        EnchantmentTagKeys.IN_ENCHANTING_TABLE,
        EnchantmentTagKeys.TREASURE
    ),
    exclusiveWith = setOf(EnchantmentKeys.LOOTING, EnchantmentKeys.SILK_TOUCH),
    activeSlots = setOf(EquipmentSlotGroup.HAND),
    maxLevel = 3,
    listeners = setOf(ExperienceListener)
), ExperienceEnchantment