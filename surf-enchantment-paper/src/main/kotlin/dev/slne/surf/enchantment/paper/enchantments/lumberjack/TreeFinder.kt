package dev.slne.surf.enchantment.paper.enchantments.lumberjack

import dev.slne.surf.api.core.util.objectListOf
import dev.slne.surf.api.paper.pdc.block.pdc
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.Tag
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.block.data.type.Leaves
import org.bukkit.persistence.PersistentDataType

object TreeFinder {

    private val MODIFIED_BLOCK_KEY = NamespacedKey("surf", "skill_modified_block")

    private val directions = objectListOf(
        BlockFace.UP, BlockFace.DOWN,
        BlockFace.NORTH, BlockFace.SOUTH,
        BlockFace.EAST, BlockFace.WEST
    )

    fun isEligibleBlock(block: Block): Boolean {
        return block.pdc().get(MODIFIED_BLOCK_KEY, PersistentDataType.BOOLEAN) != true
    }

    fun findTree(
        start: Block,
        maxBlocks: Int = LumberjackEnchantmentImpl.MAX_LEVEL * LumberjackEnchantmentImpl.BLOCKS_PER_LEVEL,
        includeLeaves: Boolean = LumberjackEnchantmentImpl.INCLUDE_LEAVES
    ): Set<Block> {
        val result = LinkedHashSet<Block>()
        val queue: ArrayDeque<Block> = ArrayDeque()

        val logType = start.type

        val baseLogs = detect2x2(start, logType)
        if (baseLogs.isNotEmpty()) {
            queue.addAll(baseLogs)
        } else {
            queue.add(start)
        }

        while (queue.isNotEmpty() && result.size < maxBlocks) {
            val current = queue.removeFirst()
            if (!isTreeBlock(current, logType, includeLeaves)) continue
            if (!result.add(current)) continue

            for (face in directions) {
                val relative = current.getRelative(face)
                if (isTreeBlock(relative, logType, includeLeaves)) {
                    queue.add(relative)
                }
            }
        }

        return result
    }

    private fun isTreeBlock(block: Block, logType: Material, includeLeaves: Boolean): Boolean {
        if (!isEligibleBlock(block)) return false

        val type = block.type
        if (type == logType) return true

        if (!includeLeaves || !Tag.LEAVES.isTagged(type)) return false

        val data = block.blockData as? Leaves ?: return false
        return data.distance < 7
    }

    private fun detect2x2(start: Block, logType: Material): List<Block> {
        val variants = listOf(
            listOf(0 to 0, 1 to 0, 0 to 1, 1 to 1),
            listOf(0 to 0, -1 to 0, 0 to 1, -1 to 1),
            listOf(0 to 0, 1 to 0, 0 to -1, 1 to -1),
            listOf(0 to 0, -1 to 0, 0 to -1, -1 to -1)
        )

        for (variant in variants) {
            val blocks = variant.map { (dx, dz) ->
                start.world.getBlockAt(start.x + dx, start.y, start.z + dz)
            }

            if (blocks.all { it.type == logType && isEligibleBlock(it) }) {
                return blocks
            }
        }

        return emptyList()
    }
}