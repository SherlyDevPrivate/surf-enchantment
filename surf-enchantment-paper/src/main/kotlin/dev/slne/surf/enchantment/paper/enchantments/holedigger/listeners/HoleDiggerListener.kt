package dev.slne.surf.enchantment.paper.enchantments.holedigger.listeners

import com.github.benmanes.caffeine.cache.Caffeine
import com.sksamuel.aedile.core.expireAfterWrite
import dev.slne.surf.enchantment.api.enchantments.HoleDiggerEnchantment
import dev.slne.surf.enchantment.api.utils.hasCustomEnchantment
import dev.slne.surf.enchantment.paper.enchantments.holedigger.BlockCalculator
import dev.slne.surf.enchantment.paper.enchantments.holedigger.HoleDiggerAnimation
import dev.slne.surf.enchantment.paper.enchantments.holedigger.PickaxeProperties
import dev.slne.surf.enchantment.paper.utils.BlockBreakHandler
import dev.slne.surf.enchantment.paper.utils.BlockHandler
import dev.slne.surf.enchantment.paper.utils.CooldownHandler
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.block.BlockFace
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.player.PlayerInteractEvent
import java.util.*
import kotlin.math.ceil
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

object HoleDiggerListener : Listener {
    private val blockHandler = BlockHandler()
    val cooldownHandler = CooldownHandler({
        appendSuccessPrefix()
        success("Die Wucht des Hole Diggers ist zurückgekehrt!")
    }, { secondsLeft ->
        appendErrorPrefix()
        error("Die Kraft des Hole Diggers ist erschöpft! Warte noch")
        appendSpace()
        variableValue("$secondsLeft Sekunden")
        error(".")
    }, cooldownExpiration = 10.minutes)

    private val lastBlockFace = Caffeine.newBuilder()
        .expireAfterWrite(5.minutes)
        .build<UUID, BlockFace>()

    private val CUBE_PROPERTIES = mapOf(
        Material.WOODEN_PICKAXE to PickaxeProperties(3, 3, 3),
        Material.STONE_PICKAXE to PickaxeProperties(3, 3, 3),
        Material.IRON_PICKAXE to PickaxeProperties(5, 5, 5),
        Material.COPPER_PICKAXE to PickaxeProperties(5, 5, 5),
        Material.GOLDEN_PICKAXE to PickaxeProperties(5, 5, 5),
        Material.DIAMOND_PICKAXE to PickaxeProperties(7, 7, 7),
        Material.NETHERITE_PICKAXE to PickaxeProperties(9, 9, 9)
    )

    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    fun onInteract(event: PlayerInteractEvent) {
        lastBlockFace.put(event.player.uniqueId, event.blockFace)
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun onBreak(event: BlockBreakEvent) {
        if (blockHandler.shouldAvoid(event)) return

        val player = event.player
        val item = player.inventory.itemInMainHand
        if (!item.hasCustomEnchantment<HoleDiggerEnchantment>()) return

        if (player.gameMode != GameMode.CREATIVE) {
            if (!cooldownHandler.checkCooldown(player.uniqueId)) return
        }

        val props = CUBE_PROPERTIES[item.type] ?: PickaxeProperties(3, 3, 3)
        val blockFace = lastBlockFace.getIfPresent(player.uniqueId) ?: return
        val center = BlockCalculator.getCenterBlock(event.block, blockFace, props)
        val allCalculatedBlocks = BlockCalculator.calculateBlocks(center, props)
        val targetType = event.block.type
        val sameTypeBlocks = allCalculatedBlocks.filter { it.type == targetType }
        val blockResult = blockHandler.handleBlocks(player, sameTypeBlocks)
        val blocks = blockResult.breakableBlocks.toMutableList()

        BlockBreakHandler.handleBlockBreak(
            cooldown = (blocks.sumOf { ceil(it.type.hardness).toInt() } / 20).seconds,
            blocks = blocks,
            cooldownHandler = cooldownHandler,
            event = event,
            events = blockResult.events
        )

        HoleDiggerAnimation.playSpiralEffect(center, props)
    }
}