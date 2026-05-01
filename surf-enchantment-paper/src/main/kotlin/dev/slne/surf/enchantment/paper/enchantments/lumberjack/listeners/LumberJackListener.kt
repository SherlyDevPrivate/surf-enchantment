package dev.slne.surf.enchantment.paper.enchantments.lumberjack.listeners

import dev.slne.surf.enchantment.api.enchantments.LumberJackEnchantment
import dev.slne.surf.enchantment.api.utils.getThisEnchantmentOrNull
import dev.slne.surf.enchantment.paper.enchantments.lumberjack.LumberjackEnchantmentImpl
import dev.slne.surf.enchantment.paper.enchantments.lumberjack.TreeFinder
import dev.slne.surf.enchantment.paper.utils.BlockBreakHandler
import dev.slne.surf.enchantment.paper.utils.BlockHandler
import dev.slne.surf.enchantment.paper.utils.CooldownHandler
import dev.slne.surf.enchantment.paper.utils.events.FakeBlockBreakEvent
import org.bukkit.GameMode
import org.bukkit.Tag
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import kotlin.time.Duration.Companion.milliseconds

object LumberJackListener : Listener {
    private val blockHandler = BlockHandler()

    val cooldownHandler = CooldownHandler(
        {
        appendSuccessPrefix()
        success("Die Axt ist wieder geschärft!")
    }, { secondsLeft ->
        appendErrorPrefix()
        error("Die Axt ist noch stumpf vom letzten Baum!")
        appendSpace()
        error("Bitte warte noch ")
        variableValue("$secondsLeft Sekunden")
        error(".")
    },
        allowReduction = true
    )

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun onBreak(event: BlockBreakEvent) {
        if (event is FakeBlockBreakEvent) return

        val player = event.player
        if (!player.isSneaking) return

        val item = player.inventory.itemInMainHand
        val enchantmentLevel = item.getThisEnchantmentOrNull<LumberJackEnchantment>()?.first ?: return
        val blockLimit = enchantmentLevel.coerceIn(
            1,
            LumberjackEnchantmentImpl.MAX_LEVEL
        ) * LumberjackEnchantmentImpl.BLOCKS_PER_LEVEL

        val blockToMine = event.block
        if (!Tag.LOGS.isTagged(blockToMine.type)) return
        if (!TreeFinder.isEligibleBlock(blockToMine)) return

        if (player.gameMode != GameMode.CREATIVE) {
            if (!cooldownHandler.checkCooldown(player.uniqueId)) return
        }

        val treeBlocks = TreeFinder.findTree(event.block, maxBlocks = blockLimit)

        if (treeBlocks.size <= 1) return

        val blockResult = blockHandler.handleBlocks(player, treeBlocks.toList())
        val blocksToMine = blockResult.breakableBlocks

        BlockBreakHandler.handleBlockBreak(
            cooldown = LumberjackEnchantmentImpl.COOLDOWN_MS.milliseconds,
            blocks = blocksToMine,
            cooldownHandler = cooldownHandler,
            event = event,
            events = blockResult.events
        )
    }
}