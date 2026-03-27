@file:Suppress("UnstableApiUsage")

package dev.slne.surf.enchantment.paper.enchantments.soulbound

import com.google.auto.service.AutoService
import dev.slne.surf.enchantment.api.enchantment.AbstractCustomEnchantment
import dev.slne.surf.enchantment.api.enchantments.SoulboundEnchantment
import dev.slne.surf.enchantment.api.utils.CustomItemTypeTags
import dev.slne.surf.enchantment.paper.enchantments.soulbound.listeners.SoulboundListener
import dev.slne.surf.surfapi.core.api.messages.adventure.key
import dev.slne.surf.surfapi.core.api.messages.adventure.text
import dev.slne.surf.surfapi.core.api.rarity.Rarity
import io.papermc.paper.registry.data.EnchantmentRegistryEntry
import org.bukkit.enchantments.Enchantment

@AutoService(SoulboundEnchantment::class)
class SoulboundEnchantmentImpl : AbstractCustomEnchantment(
    key = key("surf", "soulbound"),
    displayName = text("Soulbound"),
    rarity = Rarity.LEGENDARY,
    description = {
        line {
            darkSpacer("Behalte das Item auch nach dem Tod")
        }
    },
    supportedItems = CustomItemTypeTags.TOOLS_AND_ARMOR_AND_EQUIPMENT_ESSENTIALS_KEY.tagKey,
    weight = 2,
    minimumCost = EnchantmentRegistryEntry.EnchantmentCost.of(
        15,
        9
    ),
    maximumCost = EnchantmentRegistryEntry.EnchantmentCost.of(
        65,
        9
    ),
    exclusiveWith = setOf(
        Enchantment.BINDING_CURSE.key(),
        Enchantment.VANISHING_CURSE.key()
    ),
    listeners = setOf(SoulboundListener)
), SoulboundEnchantment