package dev.slne.surf.enchantment.paper.enchantments.rocketsaver.listeners

import com.destroystokyo.paper.event.player.PlayerElytraBoostEvent
import dev.slne.surf.enchantment.api.enchantments.RocketSaverEnchantment
import dev.slne.surf.enchantment.api.utils.getThisActiveEnchantmentOrNull
import dev.slne.surf.enchantment.paper.enchantments.rocketsaver.RocketSaverEnchantmentImpl
import dev.slne.surf.surfapi.core.api.util.random
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

object RocketSaverListener : Listener {
    @EventHandler
    fun onPlayerElytraBoost(event: PlayerElytraBoostEvent) {
        val (level) = event.player.getThisActiveEnchantmentOrNull<RocketSaverEnchantment>()
            ?: return
        val chance = level * RocketSaverEnchantmentImpl.CHANCE_PER_LEVEL

        if (random.nextInt(0, 100) < chance) {
            event.setShouldConsume(false)
        }
    }
}