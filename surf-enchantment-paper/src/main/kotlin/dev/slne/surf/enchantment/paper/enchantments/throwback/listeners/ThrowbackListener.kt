package dev.slne.surf.enchantment.paper.enchantments.throwback.listeners

import dev.slne.surf.enchantment.api.enchantments.ThrowBackEnchantment
import dev.slne.surf.enchantment.paper.enchantments.throwback.ThrowbackEnchantmentImpl
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageByEntityEvent

object ThrowbackListener : Listener {
    @EventHandler(ignoreCancelled = true)
    fun onDamage(event: EntityDamageByEntityEvent) {
        val player = event.damager as? Player ?: return
        val entity = event.entity as? LivingEntity ?: return

        if (entity is Player) {
            return
        }

        val item = player.inventory.itemInMainHand
        val level = item.getEnchantmentLevel(ThrowBackEnchantment.bukkitEnchantment)
        if (level <= 0) {
            return
        }

        val damageMultiplier = 1.0 + (level * ThrowbackEnchantmentImpl.BOOST_PER_LEVEL / 100.0)
        event.damage *= damageMultiplier

        val direction = player.location.toVector()
            .subtract(entity.location.toVector())
            .normalize()

        val strength = when (level) {
            1 -> 0.45
            2 -> 0.60
            3 -> 0.80
            4 -> 1.05
            else -> 1.35
        }

        entity.velocity = direction.multiply(strength).setY(0.25 + level * 0.03)
    }
}