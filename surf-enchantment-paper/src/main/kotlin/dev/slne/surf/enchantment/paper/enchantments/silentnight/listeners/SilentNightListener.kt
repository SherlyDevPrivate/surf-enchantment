package dev.slne.surf.enchantment.paper.enchantments.silentnight.listeners

import dev.slne.surf.api.paper.event.cancel
import dev.slne.surf.enchantment.api.enchantments.SilentNightEnchantment
import dev.slne.surf.enchantment.api.utils.hasThisEnchantmentActive
import org.bukkit.entity.Phantom
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityTargetLivingEntityEvent

object SilentNightListener : Listener {
    @EventHandler
    fun onEntityTargetLivingEntity(event: EntityTargetLivingEntityEvent) {
        val target = event.target as? Player ?: return

        if (event.entity !is Phantom) return
        if (!target.hasThisEnchantmentActive<SilentNightEnchantment>()) return

        event.cancel()
    }
}