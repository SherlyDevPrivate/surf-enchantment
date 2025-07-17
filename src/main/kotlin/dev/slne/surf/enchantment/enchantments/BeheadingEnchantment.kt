@file:Suppress("UnstableApiUsage")

package dev.slne.surf.enchantment.enchantments

import dev.slne.surf.enchantment.enchantment.CustomEnchantment
import dev.slne.surf.enchantment.utils.CustomItemTypeTags
import dev.slne.surf.enchantment.utils.EnchantmentRarity
import dev.slne.surf.surfapi.core.api.messages.adventure.key
import dev.slne.surf.surfapi.core.api.messages.adventure.text
import dev.slne.surf.surfapi.core.api.util.objectSetOf
import io.papermc.paper.registry.keys.tags.EnchantmentTagKeys
import org.bukkit.Material
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockDropItemEvent
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.inventory.ItemStack

object BeheadingEnchantment : CustomEnchantment(
    key("surf", "beheading"),
    text("Beheading"),
    EnchantmentRarity.LEGENDARY,
    description = {
        line {
            darkSpacer("Lässt den Kopf des Opfers fallen")
        }
    },
    supportedItems = CustomItemTypeTags.TOOLS_AND_SWORDS_KEY.tagKey,
    tags = objectSetOf(
        EnchantmentTagKeys.IN_ENCHANTING_TABLE, EnchantmentTagKeys.TREASURE
    ),
    listeners = objectSetOf(Handler)
) {
    object Handler : Listener {
        @EventHandler
        fun onEntityDeath(event: EntityDeathEvent) {
            val killer = event.entity.killer ?: return

            if(!checkItemStackHasEnchantment(killer.inventory.itemInMainHand)) {
                return
            }




        }
    }

    fun EntityType.getHead() = when(this) {
        EntityType.ZOMBIE -> ItemStack(Material.ZOMBIE_HEAD)
        EntityType.CREEPER -> ItemStack(Material.CREEPER_HEAD)
        EntityType.SKELETON -> ItemStack(Material.SKELETON_SKULL)
        EntityType.PIGLIN -> ItemStack(Material.PIGLIN_HEAD)
        EntityType.ENDER_DRAGON -> ItemStack(Material.DRAGON_HEAD)
        EntityType.WITHER_SKELETON -> ItemStack(Material.WITHER_SKELETON_SKULL)
        else -> ItemStack(Material.AIR)
    }
}