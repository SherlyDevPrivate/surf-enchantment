@file:Suppress("UnstableApiUsage")

package dev.slne.surf.enchantment.enchantments

import dev.slne.surf.enchantment.enchantment.CustomEnchantment
import dev.slne.surf.enchantment.utils.CustomItemTypeTags
import dev.slne.surf.enchantment.utils.EnchantmentRarity
import dev.slne.surf.surfapi.core.api.messages.adventure.key
import dev.slne.surf.surfapi.core.api.messages.adventure.text
import io.papermc.paper.registry.keys.EnchantmentKeys
import io.papermc.paper.registry.keys.tags.EnchantmentTagKeys
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.inventory.EquipmentSlotGroup

object ExperienceEnchantment : CustomEnchantment(
    key("surf", "experience"),
    text("Experience"),
    EnchantmentRarity.LEGENDARY,
    description = {
        line {
            darkSpacer("Droppt dir mehr Erfahrung")
        }
    },
    supportedItems = CustomItemTypeTags.TOOLS_AND_WEAPONS_KEY.tagKey,
    tags = setOf(
        EnchantmentTagKeys.IN_ENCHANTING_TABLE,
        EnchantmentTagKeys.TREASURE
    ),
    exclusiveWith = setOf(EnchantmentKeys.LOOTING, EnchantmentKeys.SILK_TOUCH),
    activeSlots = setOf(EquipmentSlotGroup.HAND),
    maxLevel = 3,
    listeners = setOf(Handler)
) {
    object Handler : Listener {
        @EventHandler
        fun onEntityDeath(event: EntityDeathEvent) {
            val killer = event.entity.killer ?: return
            val (level) = killer.getThisActiveEnchantmentOrNull() ?: return
            event.droppedExp = calculateDrops(event.droppedExp, level).toInt()
        }

        @EventHandler
        fun onEntityDeath(event: BlockBreakEvent) {
            val (level) = event.player.getThisActiveEnchantmentOrNull() ?: return
            event.expToDrop = calculateDrops(event.expToDrop, level).toInt()
        }
    }

    private fun calculateDrops(xp: Int, level: Int): Double {
        return (xp * (level / 10.0)) + xp
    }
}