@file:Suppress("UnstableApiUsage")

package dev.slne.surf.enchantment.enchantments

import dev.slne.surf.enchantment.enchantment.CustomEnchantment
import dev.slne.surf.enchantment.utils.CustomItemTypeTags
import dev.slne.surf.surfapi.core.api.messages.adventure.key
import dev.slne.surf.surfapi.core.api.util.objectSetOf
import io.papermc.paper.registry.keys.EnchantmentKeys
import io.papermc.paper.registry.keys.tags.EnchantmentTagKeys
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockDropItemEvent
import org.bukkit.inventory.ItemRarity

object TelekinesisEnchantment : CustomEnchantment(
    key = key("surf", "telekinesis"),
    displayName = { warning("Telekinesis") },
    rarity = ItemRarity.UNCOMMON,
    description = {
        line { darkSpacer("Test Beschreibung") }
    },
    supportedItems = CustomItemTypeTags.TOOLS_KEY.tagKey,
    exclusiveWith = objectSetOf(EnchantmentKeys.MENDING),
    tags = objectSetOf(EnchantmentTagKeys.CURSE),
    listeners = objectSetOf(Handler)
) {
    object Handler : Listener {
        @EventHandler
        fun onBlockBreak(event: BlockDropItemEvent) {
            val player = event.player

            event.items.forEach { drop ->
                val notAdded = player.inventory.addItem(drop.itemStack)

                notAdded.forEach { key, item ->
                    event.block.location.world.dropItem(event.block.location, item)
                }
            }

            event.items.clear()
        }
    }
}