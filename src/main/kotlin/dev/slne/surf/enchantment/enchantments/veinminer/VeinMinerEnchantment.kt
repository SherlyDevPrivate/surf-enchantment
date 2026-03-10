package dev.slne.surf.enchantment.enchantments.veinminer

import com.github.benmanes.caffeine.cache.Caffeine
import dev.slne.surf.enchantment.enchantment.CustomEnchantment
import dev.slne.surf.enchantment.enchantment.EnchantmentJob
import dev.slne.surf.enchantment.enchantments.TelekinesisEnchantment
import dev.slne.surf.enchantment.utils.CustomItemTypeTags
import dev.slne.surf.enchantment.utils.Dropper
import dev.slne.surf.enchantment.utils.EnchantmentRarity
import dev.slne.surf.surfapi.bukkit.api.extensions.server
import dev.slne.surf.surfapi.core.api.messages.adventure.key
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import dev.slne.surf.surfapi.core.api.messages.adventure.text
import io.papermc.paper.registry.keys.tags.EnchantmentTagKeys
import org.bukkit.GameMode
import org.bukkit.Material
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
                            success("Der Vein Miner ist wieder bereit!")
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

            if (!event.checkCooldown()) return

            val vein = findVein(block)
            if (vein.size <= 1) return

            val blockResult = blockHandler.handleBlocks(player, vein.toList())
            val breakable = blockResult.breakableBlocks.take(MAX_ORES_TO_MINE)

            if (breakable.isEmpty()) return

            player.applyCooldown(breakable.size * 2.toLong())

            event.applyItemDamage(breakable)

            val telekinesis = item.hasEnchantment(TelekinesisEnchantment)
            val drops = mutableListOf<ItemStack>()
            var totalExp = event.expToDrop

            for (breakEvent in blockResult.events) {
                if (breakEvent.isCancelled || !breakable.contains(breakEvent.block)) continue

                val b = breakEvent.block
                drops.addAll(b.getDrops(item))
                totalExp += breakEvent.expToDrop
                b.type = Material.AIR
            }

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
            if (lastUsage.getIfPresent(player.uniqueId) != null) {
                if (lastMessage.getIfPresent(player.uniqueId) == null) {
                    player.sendText {
                        appendErrorPrefix()
                        error("Der Vein Miner lädt noch auf!")
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
    }
}