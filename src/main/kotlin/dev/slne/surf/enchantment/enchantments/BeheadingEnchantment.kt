@file:Suppress("UnstableApiUsage")

package dev.slne.surf.enchantment.enchantments

import dev.slne.surf.enchantment.enchantment.CustomEnchantment
import dev.slne.surf.enchantment.utils.EnchantmentRarity
import dev.slne.surf.surfapi.core.api.messages.adventure.key
import dev.slne.surf.surfapi.core.api.messages.adventure.text
import dev.slne.surf.surfapi.core.api.util.objectSetOf
import dev.slne.surf.surfapi.core.api.util.random
import io.papermc.paper.registry.keys.EnchantmentKeys
import io.papermc.paper.registry.keys.tags.EnchantmentTagKeys
import io.papermc.paper.registry.keys.tags.ItemTypeTagKeys
import org.bukkit.entity.EntityType
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.inventory.EquipmentSlotGroup
import org.bukkit.inventory.ItemType

object BeheadingEnchantment : CustomEnchantment(
    key("surf", "beheading"),
    text("Beheading"),
    EnchantmentRarity.EPIC,
    description = {
        line {
            darkSpacer("Lässt den Kopf des Opfers fallen")
        }
    },
    supportedItems = ItemTypeTagKeys.ENCHANTABLE_WEAPON,
    activeSlots = setOf(EquipmentSlotGroup.MAINHAND),
    tags = objectSetOf(
        EnchantmentTagKeys.IN_ENCHANTING_TABLE,
        EnchantmentTagKeys.TREASURE
    ),
    exclusiveWith = setOf(EnchantmentKeys.LOOTING),
    maxLevel = 3,
    listeners = objectSetOf(Handler)
) {
    object Handler : Listener {
        @EventHandler
        fun onEntityDeath(event: EntityDeathEvent) {
            val entity = event.entity
            val killer = event.entity.killer ?: return
            val (level) = killer.getThisActiveEnchantmentOrNull() ?: return

            val chance = level * 2
            if (random.nextInt(0, 100) < chance) {
                event.drops.add(entity.type.getHead().createItemStack())
            }

            ItemType.SKELETON_SKULL
        }
    }

    fun EntityType.getHead() = when (this) {
        EntityType.ZOMBIE -> ItemType.ZOMBIE_HEAD
        EntityType.CREEPER -> ItemType.CREEPER_HEAD
        EntityType.SKELETON -> ItemType.SKELETON_SKULL
        EntityType.PIGLIN -> ItemType.PIGLIN_HEAD
        EntityType.ENDER_DRAGON -> ItemType.DRAGON_HEAD
        EntityType.WITHER_SKELETON -> ItemType.WITHER_SKELETON_SKULL
        else -> ItemType.AIR
    }
}