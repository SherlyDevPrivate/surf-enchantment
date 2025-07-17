@file:Suppress("UnstableApiUsage")

package dev.slne.surf.enchantment.enchantments

import dev.slne.surf.enchantment.enchantment.CustomEnchantment
import dev.slne.surf.enchantment.utils.CustomItemTypeTags
import dev.slne.surf.enchantment.utils.EnchantmentRarity
import dev.slne.surf.surfapi.core.api.messages.adventure.key
import dev.slne.surf.surfapi.core.api.messages.adventure.text
import dev.slne.surf.surfapi.core.api.util.objectSetOf
import io.papermc.paper.registry.keys.tags.EnchantmentTagKeys
import it.unimi.dsi.fastutil.objects.ObjectSet
import org.bukkit.enchantments.Enchantment
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.entity.EntityDeathEvent

object ExperienceEnchantment : CustomEnchantment(
    key("surf", "experience"),
    text("Experience"),
    EnchantmentRarity.LEGENDARY,
    description = {
        line {
            darkSpacer("Droppt dir mehr Erfahrung")
        }
    },
    supportedItems = CustomItemTypeTags.TOOLS_AND_SWORDS_KEY.tagKey,
    tags = objectSetOf(
        EnchantmentTagKeys.IN_ENCHANTING_TABLE, EnchantmentTagKeys.TREASURE
    ),
    exclusiveWith = ObjectSet.of(Enchantment.LOOTING.key(), Enchantment.SILK_TOUCH.key()),
    maxLevel = 3,
    listeners = objectSetOf(Handler)
) {
    object Handler : Listener {
        @EventHandler
        fun onEntityDeath(event: EntityDeathEvent) {
            val killer = event.entity.killer ?: return
            val experienceLevel = killer.inventory.itemInMainHand.enchantments[bukkitEnchantment] ?: return

            event.droppedExp = calculateDrops(event.droppedExp, experienceLevel).toInt()
        }

        @EventHandler
        fun onEntityDeath(event: BlockBreakEvent) {
            val player = event.player
            val experienceLevel = player.inventory.itemInMainHand.enchantments[bukkitEnchantment] ?: return

            event.expToDrop = calculateDrops(event.expToDrop, experienceLevel).toInt()
        }
    }

    private fun calculateDrops(xp: Int, level: Int) : Double {
        return (xp * (level / 10.0)) + xp
    }
}