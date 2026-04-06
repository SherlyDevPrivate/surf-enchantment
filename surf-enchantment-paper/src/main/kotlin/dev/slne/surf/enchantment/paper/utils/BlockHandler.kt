@file:Suppress("UnstableApiUsage")

package dev.slne.surf.enchantment.paper.utils

import dev.slne.surf.api.core.util.mutableObjectListOf
import dev.slne.surf.api.core.util.toObjectList
import dev.slne.surf.enchantment.paper.utils.events.FakeBlockBreakEvent
import it.unimi.dsi.fastutil.objects.ObjectList
import org.bukkit.block.Block
import org.bukkit.entity.Player

class BlockHandler {
    fun handleBlocks(player: Player, blocks: List<Block>): BlockResult {
        val breakableBlocks = mutableObjectListOf<Block>()
        val events = mutableObjectListOf<FakeBlockBreakEvent>()

        for (block in blocks) {
            val event = FakeBlockBreakEvent(block, player)

            events.add(event)

            if (event.callEvent()) {
                breakableBlocks.add(block)
            }
        }

        return BlockResult(
            inputBlocks = blocks.toObjectList(),
            breakableBlocks = breakableBlocks,
            events = events
        )
    }

    data class BlockResult(
        val inputBlocks: ObjectList<Block>,
        val breakableBlocks: ObjectList<Block>,
        val events: ObjectList<FakeBlockBreakEvent>
    )
}