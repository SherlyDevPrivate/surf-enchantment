@file:Suppress("UnstableApiUsage")

package dev.slne.surf.enchantment.paper.enchantments.holedigger

import com.github.benmanes.caffeine.cache.Caffeine
import com.sksamuel.aedile.core.expireAfterWrite
import dev.slne.surf.surfapi.core.api.util.mutableObjectListOf
import dev.slne.surf.surfapi.core.api.util.toObjectList
import it.unimi.dsi.fastutil.objects.ObjectList
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.event.block.BlockBreakEvent
import kotlin.time.Duration.Companion.minutes

class BlockHandler {
    private val avoidBlockBreakEvents = Caffeine.newBuilder()
        .expireAfterWrite(10.minutes)
        .build<BlockBreakEvent, Unit>()

    fun handleBlocks(player: Player, blocks: List<Block>): BlockResult {
        val breakableBlocks = mutableObjectListOf<Block>()
        val events = mutableObjectListOf<BlockBreakEvent>()

        for (block in blocks) {
            val event = BlockBreakEvent(block, player)
            avoidBlockBreakEvents.put(event, Unit)

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

    fun shouldAvoid(event: BlockBreakEvent): Boolean {
        return avoidBlockBreakEvents.getIfPresent(event) != null
    }

    data class BlockResult(
        val inputBlocks: ObjectList<Block>,
        val breakableBlocks: ObjectList<Block>,
        val events: ObjectList<BlockBreakEvent>
    )
}