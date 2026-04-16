package dev.slne.surf.enchantment.paper.enchantments.telekinesis.listeners

import com.github.shynixn.mccoroutine.folia.entityDispatcher
import com.github.shynixn.mccoroutine.folia.launch
import com.github.shynixn.mccoroutine.folia.ticks
import dev.slne.surf.enchantment.api.enchantments.telekinesis.PostTelekinesisItemEvent
import dev.slne.surf.enchantment.api.enchantments.telekinesis.TelekinesisEnchantment
import dev.slne.surf.enchantment.api.utils.hasCustomEnchantment
import dev.slne.surf.enchantment.paper.plugin
import dev.slne.surf.enchantment.paper.utils.VehicleDrops
import dev.slne.surf.surfapi.bukkit.api.extensions.server
import kotlinx.coroutines.delay
import org.bukkit.Location
import org.bukkit.entity.Player
import org.bukkit.event.Event
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockDropItemEvent
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.entity.ItemSpawnEvent
import org.bukkit.event.inventory.InventoryPickupItemEvent
import org.bukkit.event.player.PlayerShearEntityEvent
import org.bukkit.event.vehicle.VehicleDestroyEvent
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemStack
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap
import kotlin.time.Duration.Companion.milliseconds

object TelekinesisListener : Listener {

    private data class SuppressedVehicle(val playerUuid: UUID, val vehicleLocation: Location)

    private val telekinesisTargets: MutableMap<UUID, SuppressedVehicle> = ConcurrentHashMap<UUID, SuppressedVehicle>()

    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onBlockBreak(event: BlockBreakEvent) {
        val player = event.player

        if (!player.inventory.itemInMainHand.hasCustomEnchantment<TelekinesisEnchantment>()) return

        player.giveExp(event.expToDrop, true)
        event.expToDrop = 0
    }

    @EventHandler
    fun onBlockDrop(event: BlockDropItemEvent) {
        val player = event.player
        if (!player.inventory.itemInMainHand.hasCustomEnchantment<TelekinesisEnchantment>()) return

        val dropLocation = event.block.location.clone().add(0.5, 0.5, 0.5)
        val drops = event.items.map { it.itemStack }

        addDropsToInventory(player, drops, event, dropLocation)

        event.items.clear()
    }

    @EventHandler
    fun onEntityDeath(event: EntityDeathEvent) {
        val player = event.entity.killer ?: return
        if (!player.inventory.itemInMainHand.hasCustomEnchantment<TelekinesisEnchantment>()) return

        val dropLocation = event.entity.location.clone()
        val drops = event.drops.toList()

        addDropsToInventory(player, drops, event, dropLocation)

        player.giveExp(event.droppedExp, true)
        event.droppedExp = 0
        event.drops.clear()
    }

    @EventHandler
    fun onVehicleDestroy(event: VehicleDestroyEvent) {
        val player = event.attacker as? Player ?: return
        if (!player.inventory.itemInMainHand.hasCustomEnchantment<TelekinesisEnchantment>()) return

        val vehicle = event.vehicle
        val dropLocation = vehicle.location.clone()

        telekinesisTargets[vehicle.uniqueId] = SuppressedVehicle(player.uniqueId, dropLocation)

        plugin.launch(plugin.entityDispatcher(vehicle)) {
            delay(1.ticks.milliseconds)
            telekinesisTargets.remove(vehicle.uniqueId)
        }
    }
    @EventHandler(priority = EventPriority.HIGH, ignoreCancelled = true)
    fun onItemSpawn(event: ItemSpawnEvent) {
        val loc = event.location

        for (entry in telekinesisTargets.entries) {
            val (playerUuid, vehicleLocation) = entry.value
            val player = server.getPlayer(playerUuid) ?: continue

            if (loc.world == player.world && loc.distanceSquared(vehicleLocation) < 4) {
                val stack = event.entity.itemStack
                val drops = listOf(stack)

                addDropsToInventory(
                    player = player,
                    drops = drops,
                    originEvent = event,
                    dropLocation = vehicleLocation.clone()
                )

                event.isCancelled = true
            }
        }
    }

    @EventHandler
    fun onPlayerShearEntity(event: PlayerShearEntityEvent) {
        val player = event.player
        if (!player.inventory.itemInMainHand.hasCustomEnchantment<TelekinesisEnchantment>()) return

        val dropLocation = event.entity.location.clone()
        val drops = event.drops.toList()

        addDropsToInventory(player, drops, event, dropLocation)

        event.drops = emptyList()
    }

    private fun addDropsToInventory(
        player: Player,
        drops: List<ItemStack>,
        originEvent: Event,
        dropLocation: Location
    ) {
        drops.forEach { drop ->
            val notAdded = player.inventory.addItem(drop)

            val postTelekinesisItemEvent = PostTelekinesisItemEvent(
                player = player,
                itemStack = drop,
                notAddedToInventory = notAdded.toMap(),
                originEvent = originEvent
            )
            postTelekinesisItemEvent.callEvent()

            notAdded.values.forEach { item ->
                dropLocation.world.dropItemNaturally(dropLocation, item)
            }
        }
    }
}