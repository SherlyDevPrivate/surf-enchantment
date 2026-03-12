package dev.slne.surf.enchantment.paper.enchantments.holedigger

import it.unimi.dsi.fastutil.objects.ObjectArrayList
import it.unimi.dsi.fastutil.objects.ObjectList
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.BlockFace

object BlockCalculator {
    fun getCenterBlock(
        clickedBlock: Block,
        face: BlockFace,
        props: PickaxeProperties
    ): Block {
        val halfDepth = props.z / 2
        return clickedBlock.getRelative(face.oppositeFace, halfDepth)
    }

    fun calculateBlocks(
        center: Block,
        props: PickaxeProperties
    ): ObjectList<Block> {
        val result = ObjectArrayList<Block>()
        val world = center.world

        val rx = props.x / 2
        val ry = props.y / 2
        val rz = props.z / 2

        val cx = center.x
        val cy = center.y
        val cz = center.z

        for (x in (cx - rx)..(cx + rx)) {
            for (y in (cy - ry)..(cy + ry)) {
                for (z in (cz - rz)..(cz + rz)) {
                    val block = world.getBlockAt(x, y, z)

                    if (!block.isEmpty && block.type != Material.BEDROCK) {
                        result.add(block)
                    }
                }
            }
        }

        return result
    }
}