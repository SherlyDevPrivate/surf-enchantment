@file:Suppress("UnstableApiUsage")

package dev.slne.surf.enchantment.enchantments

import dev.slne.surf.enchantment.enchantment.CustomEnchantment
import dev.slne.surf.enchantment.utils.CustomItemTypeTags
import dev.slne.surf.enchantment.utils.EnchantmentRarity
import dev.slne.surf.surfapi.core.api.messages.adventure.key
import dev.slne.surf.surfapi.core.api.messages.adventure.text
import dev.slne.surf.surfapi.core.api.util.objectSetOf
import io.papermc.paper.registry.keys.tags.EnchantmentTagKeys
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerShearEntityEvent

object SheepnesisEnchantment : CustomEnchantment(
    key("surf", "sheepnesis"),
    text("Sheepnesis"),
    EnchantmentRarity.RARE,
    description = {
        line {
            darkSpacer("Drops und Erfahrung gehen direkt in dein Inventar")
        }
    },
    maxLevel = 1,
    supportedItems = CustomItemTypeTags.SHEARS_KEY.tagKey,
    tags = objectSetOf(
        EnchantmentTagKeys.IN_ENCHANTING_TABLE
    ),
    listeners = objectSetOf(Handler)
) {
    object Handler : Listener {
        @EventHandler
        fun onPlayerShearEntity(event: PlayerShearEntityEvent) {
            val player = event.player

            if (!checkItemStackHasEnchantment(player.inventory.itemInMainHand)) return

            event.drops.forEach { drop ->
                val notAdded = player.inventory.addItem(drop)

                notAdded.forEach { (_, item) ->
                    player.world.dropItem(player.location, item)
                }
            }

            event.drops.clear()
        }
    }
}