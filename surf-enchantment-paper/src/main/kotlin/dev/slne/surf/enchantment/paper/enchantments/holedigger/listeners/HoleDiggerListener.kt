package dev.slne.surf.enchantment.paper.enchantments.holedigger.listeners

import com.github.benmanes.caffeine.cache.Caffeine
import com.github.shynixn.mccoroutine.folia.launch
import com.github.shynixn.mccoroutine.folia.ticks
import com.sksamuel.aedile.core.expireAfterWrite
import dev.slne.surf.enchantment.api.enchantment.EnchantmentJob
import dev.slne.surf.enchantment.api.enchantments.HoleDiggerEnchantment
import dev.slne.surf.enchantment.api.enchantments.TelekinesisEnchantment
import dev.slne.surf.enchantment.api.utils.hasCustomEnchantment
import dev.slne.surf.enchantment.paper.enchantments.holedigger.BlockCalculator
import dev.slne.surf.enchantment.paper.enchantments.holedigger.BlockHandler
import dev.slne.surf.enchantment.paper.enchantments.holedigger.PickaxeProperties
import dev.slne.surf.enchantment.paper.plugin
import dev.slne.surf.enchantment.utils.Dropper
import dev.slne.surf.surfapi.bukkit.api.extensions.server
import dev.slne.surf.surfapi.core.api.messages.adventure.playSound
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import kotlinx.coroutines.delay
import net.kyori.adventure.sound.Sound
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ItemStack
import java.time.OffsetDateTime
import java.util.*
import kotlin.math.ceil
import kotlin.math.cos
import kotlin.math.sin
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration
import org.bukkit.Sound as BukkitSound

object HoleDiggerListener : Listener {
    private val blockHandler = BlockHandler()

    private val lastUsage = Caffeine.newBuilder()
        .expireAfterWrite(10.minutes)
        .maximumSize(10_000)
        .build<UUID, OffsetDateTime>()

    private val lastBlockFace = Caffeine.newBuilder()
        .expireAfterWrite(5.minutes)
        .build<UUID, BlockFace>()

    private val lastMessage = Caffeine.newBuilder()
        .expireAfterWrite(5.seconds.toJavaDuration())
        .build<UUID, Unit>()


    private val CUBE_PROPERTIES = mapOf(
        Material.WOODEN_PICKAXE to PickaxeProperties(3, 3, 3),
        Material.STONE_PICKAXE to PickaxeProperties(3, 3, 3),

        Material.IRON_PICKAXE to PickaxeProperties(5, 5, 5),
        Material.COPPER_PICKAXE to PickaxeProperties(5, 5, 5),
        Material.GOLDEN_PICKAXE to PickaxeProperties(5, 5, 5),

        Material.DIAMOND_PICKAXE to PickaxeProperties(7, 7, 7),

        Material.NETHERITE_PICKAXE to PickaxeProperties(9, 9, 9)
    )

    object HoleDiggerJob : EnchantmentJob() {
        override suspend fun tick() {
            val now = OffsetDateTime.now()

            lastUsage.asMap().forEach { (uuid, expireTime) ->
                if (expireTime.isBefore(now)) {
                    lastUsage.invalidate(uuid)

                    server.getPlayer(uuid)?.sendText {
                        appendSuccessPrefix()
                        success("Die Wucht des Hole Diggers ist zurückgekehrt!")
                    }
                }
            }
        }
    }

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
            if (!event.checkCooldown()) return
        }

        val props = CUBE_PROPERTIES[item.type] ?: PickaxeProperties(3, 3, 3)

        val blockFace = lastBlockFace.getIfPresent(player.uniqueId) ?: return

        val center = BlockCalculator.getCenterBlock(event.block, blockFace, props)
        val allCalculatedBlocks = BlockCalculator.calculateBlocks(center, props)
        val targetType = event.block.type
        val sameTypeBlocks = allCalculatedBlocks.filter { it.type == targetType }
        val blockResult = blockHandler.handleBlocks(player, sameTypeBlocks)
        val blocks = blockResult.breakableBlocks.toMutableList()

        if (blocks.isEmpty()) return

        if (player.gameMode != GameMode.CREATIVE) {
            player.applyCooldown(blocks.sumOf { ceil(it.type.hardness).toInt() } / 20)
            event.applyItemDamage(blocks)
        }

        val drops = blocks.flatMap { it.getDrops(item) }
        var totalExp = event.expToDrop

        for (breakEvent in blockResult.events) {
            if (breakEvent.isCancelled) continue

            val block = breakEvent.block

            block.type = Material.AIR
            totalExp += breakEvent.expToDrop
        }

        playSpiralEffect(center, props)

        val telekinesis = item.hasCustomEnchantment<TelekinesisEnchantment>()

        if (telekinesis) {
            event.handleTelekinesis(drops, totalExp)
        } else {
            event.handleNormalDrops(drops, totalExp)
        }
    }

    private fun BlockBreakEvent.checkCooldown(): Boolean {
        val expireTime = lastUsage.getIfPresent(player.uniqueId)
        if (expireTime != null) {
            if (lastMessage.getIfPresent(player.uniqueId) == null) {
                val secondsLeft =
                    expireTime.toEpochSecond() - OffsetDateTime.now().toEpochSecond()

                player.sendText {
                    appendErrorPrefix()
                    error("Die Kraft des Hole Diggers ist erschöpft! Warte noch")
                    appendSpace()
                    variableValue("$secondsLeft Sekunden")
                    error(".")
                }
                lastMessage.put(player.uniqueId, Unit)
            }
            return false
        }
        return true
    }

    private fun BlockBreakEvent.applyItemDamage(block: List<Block>) {
        val durabilityLoss = block.count { !it.type.isAir }
        if (durabilityLoss <= 0) return

        val item = player.inventory.itemInMainHand
        if (player.gameMode == GameMode.CREATIVE) return

        item.damage(durabilityLoss, player)
    }

    private fun BlockBreakEvent.handleTelekinesis(drops: List<ItemStack>, totalExp: Int) {
        drops.forEach { leftover ->
            val rest = player.inventory.addItem(leftover)

            Dropper.drop(player.location, rest.values.toList())
        }

        player.giveExp(totalExp, true)
        expToDrop = 0
    }

    private fun BlockBreakEvent.handleNormalDrops(drops: List<ItemStack>, totalExp: Int) {
        val loc = block.location.clone().add(0.5, 0.5, 0.5)

        Dropper.drop(loc, drops)
        expToDrop = totalExp
    }

    private fun Player.applyCooldown(cooldown: Int) {
        lastUsage.put(uniqueId, OffsetDateTime.now().plusSeconds(cooldown.toLong()))
    }

    private fun playSpiralEffect(centerBlock: Block, props: PickaxeProperties) {
        val center = centerBlock.location.toCenterLocation()

        val height = props.y.toDouble()
        val radiusX = props.x / 2.0
        val radiusZ = props.z / 2.0

        val points = 120
        val iterations = 4
        val pointsPerTick = 3

        val nearbyPlayers = center.getNearbyPlayers(32.0)

        nearbyPlayers.forEach { player ->
            player.playSound(true) {
                type(BukkitSound.ENTITY_ZOMBIE_VILLAGER_CURE)
                source(Sound.Source.NEUTRAL)
                pitch(0.5f)
            }
        }

        plugin.launch {
            for (step in 0 until points step pointsPerTick) {
                for (subStep in 0 until pointsPerTick) {
                    val currentStep = step + subStep
                    if (currentStep >= points) break

                    val progress = currentStep.toDouble() / points
                    val angle = 2 * Math.PI * iterations * progress

                    val x = cos(angle) * radiusX * progress
                    val z = sin(angle) * radiusZ * progress
                    val y = (progress - 0.5) * height

                    val particleLoc = center.clone().add(x, y, z)

                    nearbyPlayers.forEach { player ->
                        player.spawnParticle(
                            Particle.ENCHANT,
                            particleLoc,
                            5,
                            0.05, 0.05, 0.05, 0.01
                        )

                        player.spawnParticle(
                            Particle.TRIAL_SPAWNER_DETECTION,
                            particleLoc,
                            1,
                            0.0, 0.0, 0.0, 0.0
                        )
                    }
                }

                delay(1.ticks)
            }
        }
    }
}