package dev.slne.surf.enchantment.paper.enchantments.veinminer

import org.bukkit.block.Block
import java.util.*

object VeinFinder {
    fun findVein(start: Block): Set<Block> {
        val found = mutableSetOf<Block>()
        val queue = ArrayDeque<Block>()
        val type = start.type

        queue.add(start)
        found.add(start)

        while (queue.isNotEmpty() && found.size <= VeinMinerEnchantmentImpl.MAX_ORES_TO_MINE) {
            val current = queue.poll()
            for (x in -1..1) {
                for (y in -1..1) {
                    for (z in -1..1) {
                        if (x == 0 && y == 0 && z == 0) continue

                        val relative = current.getRelative(x, y, z)

                        if (relative.type == type && relative !in found) {
                            found.add(relative)
                            queue.add(relative)
                        }
                    }
                }
            }
        }

        return found
    }
}