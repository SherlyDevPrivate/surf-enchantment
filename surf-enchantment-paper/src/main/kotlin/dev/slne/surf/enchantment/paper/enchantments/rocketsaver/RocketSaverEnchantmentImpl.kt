package dev.slne.surf.enchantment.paper.enchantments.rocketsaver

import com.google.auto.service.AutoService
import dev.slne.surf.enchantment.api.enchantment.AbstractCustomEnchantment
import dev.slne.surf.enchantment.api.enchantments.RocketSaverEnchantment
import dev.slne.surf.enchantment.api.utils.CustomItemTypeTags
import dev.slne.surf.enchantment.api.utils.EnchantmentRarity
import dev.slne.surf.enchantment.paper.enchantments.rocketsaver.listeners.RocketSaverListener
import dev.slne.surf.surfapi.core.api.messages.adventure.key
import dev.slne.surf.surfapi.core.api.messages.adventure.text
import org.bukkit.inventory.EquipmentSlotGroup

@AutoService(RocketSaverEnchantment::class)
class RocketSaverEnchantmentImpl : AbstractCustomEnchantment(
    key = key("surf", "rocket_saver"),
    displayName = text("Rocket Saver"),
    rarity = EnchantmentRarity.MYTHIC,
    description = { level ->
        line {
            val chance = level * CHANCE_PER_LEVEL
            text("Gewährt eine ")
            text("$chance %")
            text(" Chance, um keine Feuerwerkskörper zu verbrauchen, wenn man mit dem Elytra boostet")
        }
    },
    supportedItems = CustomItemTypeTags.ELYTRA_KEY.tagKey,
    activeSlots = setOf(EquipmentSlotGroup.CHEST),
    maxLevel = 3,
    listeners = setOf(RocketSaverListener)
), RocketSaverEnchantment {
    companion object {
        const val CHANCE_PER_LEVEL = 15
    }
}