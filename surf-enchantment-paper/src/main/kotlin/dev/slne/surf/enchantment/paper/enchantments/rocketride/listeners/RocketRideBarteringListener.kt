package dev.slne.surf.enchantment.paper.enchantments.rocketride.listeners

import dev.slne.surf.api.core.messages.adventure.buildText
import dev.slne.surf.api.paper.extensions.server
import dev.slne.surf.enchantment.api.enchantments.RocketRideEnchantment
import dev.slne.surf.enchantment.paper.enchantments.rocketride.RocketRideEnchantmentImpl
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PiglinBarterEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.EnchantmentStorageMeta

object RocketRideBarteringListener : Listener {

    @EventHandler
    fun onPiglinBarter(event: PiglinBarterEvent) {
        val outcome = event.outcome

        if (Math.random() < RocketRideEnchantmentImpl.CHANCE_TO_DROP_ON_BARTER) {

            val enchantedBook = ItemStack(Material.ENCHANTED_BOOK).apply {
                itemMeta = itemMeta?.also { meta ->
                    if (meta is EnchantmentStorageMeta) {
                        meta.addStoredEnchant(
                            RocketRideEnchantment.bukkitEnchantment,
                            RocketRideEnchantment.bukkitEnchantment.maxLevel,
                            true
                        )
                    }
                }
            }
            outcome.add(enchantedBook)

            server.broadcast(buildText { error("Piglin has bartered rocket ride") })
        }
    }
}