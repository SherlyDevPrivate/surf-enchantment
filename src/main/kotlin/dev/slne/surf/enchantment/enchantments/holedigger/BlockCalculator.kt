package dev.slne.surf.enchantment.enchantments.holedigger

import dev.slne.surf.surfapi.core.api.util.objectListOf
import it.unimi.dsi.fastutil.objects.ObjectArrayList
import it.unimi.dsi.fastutil.objects.ObjectList
import org.bukkit.block.Block
import org.bukkit.block.BlockFace

object BlockCalculator {

    private fun walkCube(
        cx: Int,
        cy: Int,
        cz: Int,
        radius: Int,
        face: BlockFace
    ): Sequence<Triple<Int, Int, Int>> = sequence {

        val xr = cx - radius..cx + radius
        val yr = cy - radius..cy + radius
        val zr = cz - radius..cz + radius

        when (face) {

            BlockFace.UP -> {
                for (y in yr) {
                    for (x in xr) {
                        for (z in zr) {
                            yield(Triple(x, y, z))
                        }
                    }
                }
            }

            BlockFace.DOWN -> {
                for (y in yr.reversed()) {
                    for (x in xr) {
                        for (z in zr) {
                            yield(Triple(x, y, z))
                        }
                    }
                }
            }

            BlockFace.NORTH -> {
                for (z in zr) {
                    for (x in xr) {
                        for (y in yr) {
                            yield(Triple(x, y, z))
                        }
                    }
                }
            }

            BlockFace.SOUTH -> {
                for (z in zr.reversed()) {
                    for (x in xr) {
                        for (y in yr) {
                            yield(Triple(x, y, z))
                        }
                    }
                }
            }

            BlockFace.EAST -> {
                for (x in xr.reversed()) {
                    for (y in yr) {
                        for (z in zr) {
                            yield(Triple(x, y, z))
                        }
                    }
                }
            }

            BlockFace.WEST -> {
                for (x in xr) {
                    for (y in yr) {
                        for (z in zr) {
                            yield(Triple(x, y, z))
                        }
                    }
                }
            }

            else -> {}
        }
    }

    fun calculateBlocks(startingBlock: Block, face: BlockFace, radius: Int): ObjectList<Block> {
        val world = startingBlock.world
        val type = startingBlock.type

        val sx = startingBlock.x
        val sy = startingBlock.y
        val sz = startingBlock.z

        val (cx, cy, cz) = when (face) {
            BlockFace.UP -> Triple(sx, sy - radius, sz)
            BlockFace.DOWN -> Triple(sx, sy + radius, sz)
            BlockFace.NORTH -> Triple(sx, sy, sz + radius)
            BlockFace.SOUTH -> Triple(sx, sy, sz - radius)
            BlockFace.EAST -> Triple(sx - radius, sy, sz)
            BlockFace.WEST -> Triple(sx + radius, sy, sz)
            else -> return objectListOf()
        }

        val result = ObjectArrayList<Block>()

        for ((x, y, z) in walkCube(cx, cy, cz, radius, face)) {
            val block = world.getBlockAt(x, y, z)

            if (block.type == type) {
                result.add(block)
            }
        }

        return result
    }
}