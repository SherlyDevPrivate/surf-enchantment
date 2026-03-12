package dev.slne.surf.enchantment.paper.enchantments.soulbound.listeners

import dev.slne.surf.enchantment.api.enchantments.SoulboundEnchantment
import dev.slne.surf.enchantment.api.utils.hasCustomEnchantment
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent

object SoulboundListener : Listener {
    @EventHandler
    fun onEntityDeath(event: PlayerDeathEvent) {
        val dropIt = event.drops.iterator()
        dropIt.forEachRemaining { drop ->
            if (drop.hasCustomEnchantment<SoulboundEnchantment>()) {
                dropIt.remove()
                event.itemsToKeep.add(drop)
            }
        }
    }
}