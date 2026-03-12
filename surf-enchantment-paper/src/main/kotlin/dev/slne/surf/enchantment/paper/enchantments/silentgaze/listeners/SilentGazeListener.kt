package dev.slne.surf.enchantment.paper.enchantments.silentgaze.listeners

import dev.slne.surf.enchantment.api.enchantments.SilentGazeEnchantment
import dev.slne.surf.enchantment.api.utils.hasThisEnchantmentActive
import dev.slne.surf.surfapi.bukkit.api.event.cancel
import org.bukkit.entity.Enderman
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityTargetLivingEntityEvent

object SilentGazeListener : Listener {
    @EventHandler
    fun onEntityTargetLivingEntity(event: EntityTargetLivingEntityEvent) {
        val target = event.target as? Player ?: return
        if (event.entity !is Enderman) return
        if (!target.hasThisEnchantmentActive<SilentGazeEnchantment>()) return
        event.cancel()
    }
}