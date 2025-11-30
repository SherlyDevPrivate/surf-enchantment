package dev.slne.surf.enchantment.enchantments

import dev.slne.surf.enchantment.enchantment.CustomEnchantment
import dev.slne.surf.enchantment.utils.EnchantmentRarity
import dev.slne.surf.surfapi.bukkit.api.event.cancel
import dev.slne.surf.surfapi.core.api.messages.adventure.key
import dev.slne.surf.surfapi.core.api.messages.adventure.text
import io.papermc.paper.registry.keys.tags.ItemTypeTagKeys
import org.bukkit.entity.Enderman
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityTargetLivingEntityEvent
import org.bukkit.inventory.EquipmentSlotGroup

object SilentGazeEnchantment : CustomEnchantment(
    key("surf", "silent_gaze"),
    text("Silent Gaze"),
    EnchantmentRarity.EPIC,
    {
        line {
            darkSpacer("Ein Enderman wird dich nicht angreifen")
        }
    },
    ItemTypeTagKeys.ENCHANTABLE_HEAD_ARMOR,
    exclusiveWith = setOf(SilentNightEnchantment.key),
    activeSlots = setOf(EquipmentSlotGroup.HEAD),
    listeners = setOf(Handler),
) {
    object Handler : Listener {
        @EventHandler
        fun onEntityTargetLivingEntity(event: EntityTargetLivingEntityEvent) {
            val target = event.target as? Player ?: return
            if (event.entity !is Enderman) return
            if (!target.hasThisEnchantmentActive()) return
            event.cancel()
        }
    }
}