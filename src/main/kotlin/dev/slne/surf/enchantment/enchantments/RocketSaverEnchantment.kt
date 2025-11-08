package dev.slne.surf.enchantment.enchantments

import com.destroystokyo.paper.event.player.PlayerElytraBoostEvent
import dev.slne.surf.enchantment.enchantment.CustomEnchantment
import dev.slne.surf.enchantment.utils.CustomItemTypeTags
import dev.slne.surf.enchantment.utils.EnchantmentRarity
import dev.slne.surf.surfapi.core.api.messages.adventure.key
import dev.slne.surf.surfapi.core.api.messages.adventure.text
import dev.slne.surf.surfapi.core.api.util.random
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.inventory.EquipmentSlotGroup

private const val CHANCE_PER_LEVEL = 15

object RocketSaverEnchantment : CustomEnchantment(
    key("surf", "rocket_saver"),
    text("Rocket Saver"),
    EnchantmentRarity.MYTHIC,
    { level ->
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
    listeners = setOf(Handler)
) {
    object Handler : Listener {
        @EventHandler
        fun onPlayerElytraBoost(event: PlayerElytraBoostEvent) {
            val (level) = event.player.getThisActiveEnchantmentOrNull() ?: return
            val chance = level * CHANCE_PER_LEVEL
            if (random.nextInt(0, 100) < chance) {
                event.setShouldConsume(false)
            }
        }
    }
}