package dev.slne.surf.enchantment.enchantments.holedigger

import org.bukkit.Location
import org.bukkit.inventory.ItemStack

object Dropper {
    fun drop(location: Location, drops: List<ItemStack>) {
        val stacked = stackDrops(drops)

        stacked.forEach { drop ->
            location.world.dropItem(location, drop)
        }
    }

    private fun stackDrops(drops: List<ItemStack>): List<ItemStack> {
        val result = mutableListOf<ItemStack>()

        for (drop in drops) {
            var remaining = drop.amount

            for (existing in result) {
                if (!existing.isSimilar(drop)) continue

                val space = existing.maxStackSize - existing.amount
                val toAdd = minOf(space, remaining)

                existing.amount += toAdd
                remaining -= toAdd

                if (remaining <= 0) break
            }

            while (remaining > 0) {
                val clone = drop.clone()
                val amount = minOf(clone.maxStackSize, remaining)
                clone.amount = amount
                result.add(clone)
                remaining -= amount
            }
        }

        return result
    }
}