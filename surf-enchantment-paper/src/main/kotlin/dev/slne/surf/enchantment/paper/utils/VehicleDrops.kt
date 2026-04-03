package dev.slne.surf.enchantment.paper.utils

import org.bukkit.Material
import org.bukkit.entity.Boat
import org.bukkit.entity.Minecart
import org.bukkit.entity.Vehicle
import org.bukkit.inventory.InventoryHolder
import org.bukkit.inventory.ItemStack

object VehicleDrops {

    fun getDrops(vehicle: Vehicle, dropInventory: Boolean = true): List<ItemStack> {
        val drops = mutableListOf<ItemStack>()

        getBaseDrop(vehicle)?.let { drops += it }

        if (dropInventory && vehicle is InventoryHolder) {
            drops += vehicle.inventory.contents
                .filterNotNull()
                .filter { it.type != Material.AIR }
                .map { it.clone() }
        }

        return drops
    }

    private fun getBaseDrop(vehicle: Vehicle): ItemStack? {
        val material = when (vehicle) {
            is Boat -> vehicle.boatMaterial
            is Minecart -> vehicle.minecartMaterial
            else -> null
        }

        return material?.let { ItemStack(it) }
    }
}