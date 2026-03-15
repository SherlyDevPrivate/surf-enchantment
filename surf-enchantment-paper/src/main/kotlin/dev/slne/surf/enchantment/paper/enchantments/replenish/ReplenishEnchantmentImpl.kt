package dev.slne.surf.enchantment.paper.enchantments.replenish

import com.google.auto.service.AutoService
import dev.slne.surf.enchantment.api.enchantment.AbstractCustomEnchantment
import dev.slne.surf.enchantment.api.enchantments.ReplenishEnchantment
import dev.slne.surf.enchantment.paper.enchantments.replenish.listeners.ReplenishListener
import dev.slne.surf.surfapi.core.api.messages.adventure.key
import dev.slne.surf.surfapi.core.api.messages.adventure.text
import dev.slne.surf.surfapi.core.api.rarity.Rarity
import io.papermc.paper.registry.keys.tags.EnchantmentTagKeys
import io.papermc.paper.registry.keys.tags.ItemTypeTagKeys
import org.bukkit.inventory.EquipmentSlotGroup

@AutoService(ReplenishEnchantment::class)
class ReplenishEnchantmentImpl : AbstractCustomEnchantment(
    key = key("surf", "replenish"),
    displayName = text("Replenish"),
    rarity = Rarity.EPIC,
    description = {
        line {
            darkSpacer("Nutzpflanzen werden nach dem Ernten automatisch nachgepflanzt")
        }
    },
    supportedItems = ItemTypeTagKeys.HOES,
    activeSlots = setOf(EquipmentSlotGroup.MAINHAND),
    tags = setOf(
        EnchantmentTagKeys.IN_ENCHANTING_TABLE, EnchantmentTagKeys.TREASURE
    ),
    maxLevel = 1,
    listeners = setOf(ReplenishListener)
), ReplenishEnchantment