import dev.slne.surf.enchantment.paper.enchantments.lumberjack.LumberjackEnchantmentImpl
import dev.slne.surf.surfapi.core.api.util.objectListOf
import org.bukkit.Material
import org.bukkit.Tag
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.block.data.type.Leaves

object TreeFinder { //TODO: this logic may need to be optimized

    private val directions = objectListOf(
        BlockFace.UP, BlockFace.DOWN,
        BlockFace.NORTH, BlockFace.SOUTH,
        BlockFace.EAST, BlockFace.WEST
    )

    fun findTree(
        start: Block,
        maxBlocks: Int = LumberjackEnchantmentImpl.MAX_BLOCKS_TO_MINE,
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
            if (!result.add(current)) continue

            for (face in directions) {
                val relative = current.getRelative(face)
                if (result.contains(relative)) continue

                val type = relative.type
                val isLog = type == logType

                if (isLog) {
                    queue.add(relative)
                    continue
                }

                if (includeLeaves && Tag.LEAVES.isTagged(type)) {
                    val data = relative.blockData
                    if (data is Leaves && data.distance < 7) {
                        queue.add(relative)
                    }
                }
            }
        }

        return result
    }

    private fun detect2x2(start: Block, logType: Material): List<Block> {
        val offsets = listOf(
            0 to 0,
            1 to 0,
            0 to 1,
            1 to 1
        )

        val blocks = offsets.map { (dx, dz) ->
            start.world.getBlockAt(start.x + dx, start.y, start.z + dz)
        }

        return if (blocks.all { it.type == logType }) blocks else emptyList()
    }
}