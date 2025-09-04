@file:Suppress("UnstableApiUsage")

package dev.slne.surf.enchantment.enchantments

import dev.slne.surf.enchantment.enchantment.CustomEnchantment
import dev.slne.surf.enchantment.utils.EnchantmentRarity
import dev.slne.surf.surfapi.bukkit.api.event.cancel
import dev.slne.surf.surfapi.core.api.messages.adventure.key
import dev.slne.surf.surfapi.core.api.messages.adventure.text
import dev.slne.surf.surfapi.core.api.util.objectSetOf
import io.papermc.paper.registry.keys.EnchantmentKeys
import io.papermc.paper.registry.keys.tags.EnchantmentTagKeys
import io.papermc.paper.registry.keys.tags.ItemTypeTagKeys
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityTargetLivingEntityEvent

object SilentNightEnchantment : CustomEnchantment(
    key("surf", "silent_night"),
    text("Silent Night"),
    EnchantmentRarity.LEGENDARY,
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
    listeners = setOf(Handler)
) {
    object Handler : Listener {
        @EventHandler
        fun onEntityTargetLivingEntity(event: EntityTargetLivingEntityEvent) {
            val target = event.target as? Player ?: return
            if (!target.hasThisEnchantmentActive()) return
            event.cancel()
        }
    }
}