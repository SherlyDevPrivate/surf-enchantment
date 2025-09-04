@file:Suppress("UnstableApiUsage")

package dev.slne.surf.enchantment.enchantments

import dev.slne.surf.enchantment.enchantment.CustomEnchantment
import dev.slne.surf.enchantment.utils.CustomItemTypeTags
import dev.slne.surf.enchantment.utils.EnchantmentRarity
import dev.slne.surf.surfapi.core.api.messages.adventure.key
import dev.slne.surf.surfapi.core.api.messages.adventure.text
import org.bukkit.enchantments.Enchantment
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent


object SoulboundEnchantment : CustomEnchantment(
    key("surf", "soulbound"),
    text("Soulbund"),
    EnchantmentRarity.LEGENDARY,
    description = {
        line {
            darkSpacer("Behalte das Item auch nach dem Tod")
        }
    },
    supportedItems = CustomItemTypeTags.TOOLS_AND_ARMOR_AND_EQUIPMENT_KEY.tagKey,
    exclusiveWith = setOf(
        Enchantment.BINDING_CURSE.key(),
        Enchantment.VANISHING_CURSE.key()
    ),
    listeners = setOf(Handler)
) {
    object Handler : Listener {
        @EventHandler
        fun onEntityDeath(event: PlayerDeathEvent) {
            val dropIt = event.drops.iterator()
            dropIt.forEachRemaining { drop ->
                if (drop.hasThisEnchantment()) {
                    dropIt.remove()
                    event.itemsToKeep.add(drop)
                }
            }
        }
    }
}