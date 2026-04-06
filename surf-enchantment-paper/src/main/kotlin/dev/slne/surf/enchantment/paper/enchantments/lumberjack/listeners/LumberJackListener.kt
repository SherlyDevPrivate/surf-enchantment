package dev.slne.surf.enchantment.paper.enchantments.lumberjack.listeners

import dev.slne.surf.enchantment.api.enchantments.LumberJackEnchantment
import dev.slne.surf.enchantment.api.utils.hasCustomEnchantment
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
import kotlin.time.Duration.Companion.seconds

object LumberJackListener : Listener {
    private val blockHandler = BlockHandler()

    val cooldownHandler = CooldownHandler({
        appendSuccessPrefix()
        success("Die Axt ist wieder geschärft!")
    }, { secondsLeft ->
        appendErrorPrefix()
        error("Die Axt ist noch stumpf vom letzten Baum! Warte noch")
        appendSpace()
        variableValue("$secondsLeft Sekunden")
        error("!")
    })

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun onBreak(event: BlockBreakEvent) {
        if (event is FakeBlockBreakEvent) return

        val player = event.player
        if (!player.isSneaking) return

        val item = player.inventory.itemInMainHand

        if (!item.hasCustomEnchantment<LumberJackEnchantment>()) return
        if (!Tag.LOGS.isTagged(event.block.type)) return

        if (player.gameMode != GameMode.CREATIVE) {
            if (!cooldownHandler.checkCooldown(player.uniqueId)) return
        }

        val treeBlocks = TreeFinder.findTree(event.block)

        if (treeBlocks.size <= 1) return

        val blockResult = blockHandler.handleBlocks(player, treeBlocks.toList())
        val blocksToMine = blockResult.breakableBlocks

        BlockBreakHandler.handleBlockBreak(
            cooldown = (blocksToMine.size * 1.5.toLong()).seconds,
            blocks = blocksToMine,
            cooldownHandler = cooldownHandler,
            event = event,
            events = blockResult.events
        )
    }
}