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

object RocketSaverEnchantment : CustomEnchantment(
    key("surf", "rocket_saver"),
    text("Rocket Saver"),
    EnchantmentRarity.MYTHIC,
    {
        line {
            text("Grants a chance to not consume fireworks when using Elytra.")
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
            val chance = level * 15
            if (random.nextInt(0, 100) < chance) {
                event.setShouldConsume(false)
            }
        }
    }
}