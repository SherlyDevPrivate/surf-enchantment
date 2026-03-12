package dev.slne.surf.enchantment.enchantments.veinminer

import com.github.benmanes.caffeine.cache.Caffeine
import com.github.shynixn.mccoroutine.folia.launch
import com.github.shynixn.mccoroutine.folia.ticks
import dev.slne.surf.enchantment.enchantment.CustomEnchantment
import dev.slne.surf.enchantment.enchantment.EnchantmentJob
import dev.slne.surf.enchantment.enchantments.TelekinesisEnchantment
import dev.slne.surf.enchantment.plugin
import dev.slne.surf.enchantment.utils.CustomItemTypeTags
import dev.slne.surf.enchantment.utils.Dropper
import dev.slne.surf.enchantment.utils.EnchantmentRarity
import dev.slne.surf.surfapi.bukkit.api.extensions.server
import dev.slne.surf.surfapi.core.api.messages.adventure.key
import dev.slne.surf.surfapi.core.api.messages.adventure.playSound
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import dev.slne.surf.surfapi.core.api.messages.adventure.text
import io.papermc.paper.registry.keys.tags.EnchantmentTagKeys
import kotlinx.coroutines.delay
import org.bukkit.GameMode
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.block.Block
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.inventory.ItemStack
import java.time.OffsetDateTime
import java.util.*
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration
import net.kyori.adventure.sound.Sound as AdventureSound
import org.bukkit.Sound as BukkitSound

object VeinMinerEnchantment : CustomEnchantment(
    key("surf", "vein_miner"),
    text("Vain Miner"),
    EnchantmentRarity.EPIC,
    description = {
        line { darkSpacer("Erzadern werden vollständig abgebaut") }
        line {
            darkSpacer("Es werden maximal")
            appendSpace()
            variableValue("10 Blöcke")
            appendSpace()
            darkSpacer("pro Nutzung abgebaut.")
        }
        line {
            darkSpacer("Die Abklinzeit beträgt")
            appendSpace()
            variableValue("2 Sekunden")
            appendSpace()
            darkSpacer("pro abgebautem Block.")
        }
    },
    supportedItems = CustomItemTypeTags.PICKAXES_KEY.tagKey,
    exclusiveWith = setOf(Enchantment.FORTUNE.key()),
    tags = setOf(
        EnchantmentTagKeys.TRADEABLE,
    ),
    listeners = setOf(Handler),
    jobs = setOf(Handler.Job)
) {
    object Handler : Listener {
        private val blockHandler = BlockHandler()
        private const val MAX_ORES_TO_MINE = 10

        private val lastUsage = Caffeine.newBuilder()
            .expireAfterWrite(5.minutes.toJavaDuration())
            .build<UUID, OffsetDateTime>()

        private val lastMessage = Caffeine.newBuilder()
            .expireAfterWrite(3.seconds.toJavaDuration())
            .build<UUID, Unit>()

        private val ORE_MATERIALS = setOf(
            Material.COAL_ORE, Material.DEEPSLATE_COAL_ORE,
            Material.IRON_ORE, Material.DEEPSLATE_IRON_ORE,
            Material.GOLD_ORE, Material.DEEPSLATE_GOLD_ORE,
            Material.COPPER_ORE, Material.DEEPSLATE_COPPER_ORE,
            Material.REDSTONE_ORE, Material.DEEPSLATE_REDSTONE_ORE,
            Material.LAPIS_ORE, Material.DEEPSLATE_LAPIS_ORE,
            Material.DIAMOND_ORE, Material.DEEPSLATE_DIAMOND_ORE,
            Material.EMERALD_ORE, Material.DEEPSLATE_EMERALD_ORE,
            Material.NETHER_QUARTZ_ORE, Material.NETHER_GOLD_ORE,
            Material.ANCIENT_DEBRIS
        )

        object Job : EnchantmentJob() {
            override suspend fun tick() {
                val now = OffsetDateTime.now()
                lastUsage.asMap().forEach { (uuid, expireTime) ->
                    if (expireTime.isBefore(now)) {
                        lastUsage.invalidate(uuid)
                        server.getPlayer(uuid)?.sendText {
                            appendSuccessPrefix()
                            success("Vein Miner ist wieder bereit für den nächsten Schlag!")
                        }
                    }
                }
            }
        }

        @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
        fun onBreak(event: BlockBreakEvent) {
            if (blockHandler.shouldAvoid(event)) return

            val player = event.player
            val item = player.inventory.itemInMainHand
            if (!item.hasThisEnchantment()) return

            val block = event.block
            if (block.type !in ORE_MATERIALS) return

            if (player.gameMode != GameMode.CREATIVE) {
                if (!event.checkCooldown()) return
            }

            val vein = findVein(block)
            if (vein.size <= 1) return

            val blockResult = blockHandler.handleBlocks(player, vein.toList())
            val breakable = blockResult.breakableBlocks.take(MAX_ORES_TO_MINE)

            if (breakable.isEmpty()) return

            if (player.gameMode != GameMode.CREATIVE) {
                player.applyCooldown(breakable.size * 2.toLong())
                event.applyItemDamage(breakable)
            }

            val drops = mutableListOf<ItemStack>()
            var totalExp = event.expToDrop

            for (breakEvent in blockResult.events) {
                if (breakEvent.isCancelled || !breakable.contains(breakEvent.block)) continue

                val block = breakEvent.block
                drops.addAll(block.getDrops(item))
                totalExp += breakEvent.expToDrop
                block.type = Material.AIR
            }

            playVeinEffect(breakable)

            val telekinesis = item.hasEnchantment(TelekinesisEnchantment)
            if (telekinesis) {
                event.handleTelekinesis(drops, totalExp)
            } else {
                event.handleNormalDrops(drops, totalExp)
            }
        }

        private fun findVein(start: Block): Set<Block> {
            val found = mutableSetOf<Block>()
            val queue = ArrayDeque<Block>()
            val type = start.type

            queue.add(start)
            found.add(start)

            while (queue.isNotEmpty() && found.size <= MAX_ORES_TO_MINE) {
                val current = queue.poll()
                for (x in -1..1) {
                    for (y in -1..1) {
                        for (z in -1..1) {
                            if (x == 0 && y == 0 && z == 0) continue
                            val relative = current.getRelative(x, y, z)
                            if (relative.type == type && relative !in found) {
                                found.add(relative)
                                queue.add(relative)
                            }
                        }
                    }
                }
            }
            return found
        }

        private fun BlockBreakEvent.checkCooldown(): Boolean {
            val expireTime = lastUsage.getIfPresent(player.uniqueId)
            if (expireTime != null) {
                if (lastMessage.getIfPresent(player.uniqueId) == null) {
                    val secondsLeft = expireTime.toEpochSecond() - OffsetDateTime.now().toEpochSecond()
                    player.sendText {
                        appendErrorPrefix()
                        error("Zu viele Erze auf einmal! Warte noch")
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

        private fun BlockBreakEvent.applyItemDamage(blocks: List<Block>) {
            if (player.gameMode == GameMode.CREATIVE) return
            val durabilityLoss = blocks.size
            player.inventory.itemInMainHand.damage(durabilityLoss, player)
        }

        private fun BlockBreakEvent.handleTelekinesis(drops: List<ItemStack>, totalExp: Int) {
            drops.forEach { drop ->
                val rest = player.inventory.addItem(drop)
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

        private fun Player.applyCooldown(seconds: Long) {
            lastUsage.put(uniqueId, OffsetDateTime.now().plusSeconds(seconds))
        }

        private fun playVeinEffect(blocks: List<Block>) {
            if (blocks.isEmpty()) return

            val firstLocation = blocks.first().location.clone().toCenterLocation()
            val nearbyPlayers = firstLocation.getNearbyPlayers(32.0)
            if (nearbyPlayers.isEmpty()) return

            plugin.launch {
                blocks.forEachIndexed { index, block ->
                    val loc = block.location.toCenterLocation()
                    val data = block.blockData

                    nearbyPlayers.forEach { player ->
                        player.playSound(true) {
                            type(BukkitSound.BLOCK_MEDIUM_AMETHYST_BUD_BREAK)
                            source(AdventureSound.Source.NEUTRAL)
                            pitch(0.5f + (index * 0.1f).coerceAtMost(1.5f))
                        }


                        player.spawnParticle(
                            Particle.BLOCK,
                            loc,
                            15,
                            0.15,
                            0.15,
                            0.15,
                            0.05,
                            data
                        )
                        player.spawnParticle(
                            Particle.SCRAPE,
                            loc,
                            3,
                            0.1,
                            0.1,
                            0.1,
                            0.01
                        )

                        if (index < blocks.size - 1) {
                            val nextLoc = blocks[index + 1].location.toCenterLocation()
                            drawEnergyLine(player, loc, nextLoc)
                        }
                    }

                    delay(1.ticks)
                }
            }
        }

        private fun drawEnergyLine(player: Player, from: Location, to: Location) {
            val distance = from.distance(to)
            val points = (distance * 2).toInt().coerceAtLeast(1)
            val vector = to.toVector().subtract(from.toVector()).multiply(1.0 / points)

            val current = from.clone()
            for (i in 0 until points) {
                current.add(vector)
                player.spawnParticle(
                    Particle.ELECTRIC_SPARK,
                    current,
                    1,
                    0.0,
                    0.0,
                    0.0,
                    0.0
                )
            }
        }
    }
}