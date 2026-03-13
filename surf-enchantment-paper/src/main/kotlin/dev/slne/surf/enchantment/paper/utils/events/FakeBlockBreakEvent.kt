@file:Suppress("UnstableApiUsage")

package dev.slne.surf.enchantment.paper.utils.events

import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.event.block.BlockBreakEvent

class FakeBlockBreakEvent(
    block: Block,
    player: Player
) : BlockBreakEvent(block, player)