@file:Suppress("UnstableApiUsage")

package dev.slne.surf.enchantment.enchantments.holedigger

import com.github.benmanes.caffeine.cache.Caffeine
import com.sksamuel.aedile.core.expireAfterWrite
import dev.slne.surf.enchantment.enchantment.CustomEnchantment
import dev.slne.surf.enchantment.enchantment.EnchantmentJob
import dev.slne.surf.enchantment.enchantments.TelekinesisEnchantment
import dev.slne.surf.enchantment.enchantments.veinminer.VeinMinerEnchantment
import dev.slne.surf.enchantment.utils.CustomItemTypeTags
import dev.slne.surf.enchantment.utils.Dropper
import dev.slne.surf.enchantment.utils.EnchantmentRarity
import dev.slne.surf.enchantment.utils.calculateDurability
import dev.slne.surf.surfapi.bukkit.api.extensions.server
import dev.slne.surf.surfapi.core.api.messages.adventure.key
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import dev.slne.surf.surfapi.core.api.messages.adventure.text
import io.papermc.paper.registry.keys.tags.EnchantmentTagKeys
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.BlockFace
import org.bukkit.enchantments.Enchantment
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
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

object HoleDiggerEnchantment : CustomEnchantment(
    key("surf", "hole_digger"),
    text("Hole Digger"),
    EnchantmentRarity.LEGENDARY,
    description = {
        line { darkSpacer("Gräbt ein Loch in definierter Größe") }
    },
    supportedItems = CustomItemTypeTags.PICKAXES_KEY.tagKey,
    exclusiveWith = setOf(
        Enchantment.FORTUNE.key(),
        VeinMinerEnchantment.key
    ),
    tags = setOf(EnchantmentTagKeys.TREASURE),
    listeners = setOf(Handler),
    jobs = setOf(Handler.Job)
) {
    object Handler : Listener {
        private val blockHandler = BlockHandler()

        private val lastUsage = Caffeine.newBuilder()
            .expireAfterWrite(10.minutes)
            .maximumSize(10_000)
            .build<UUID, OffsetDateTime>()

        private val lastBlockBlockFace = Caffeine.newBuilder()
            .expireAfterWrite(5.minutes)
            .build<UUID, BlockFace>()

        private val lastMessage = Caffeine.newBuilder()
            .expireAfterWrite(5.seconds.toJavaDuration())
            .build<UUID, Unit>()


        private val CUBE_SIZE = mapOf(
            Material.WOODEN_PICKAXE to 3,
            Material.STONE_PICKAXE to 4,
            Material.IRON_PICKAXE to 5,
            Material.GOLDEN_PICKAXE to 6,
            Material.DIAMOND_PICKAXE to 7,
            Material.NETHERITE_PICKAXE to 8
        )

        object Job : EnchantmentJob() {
            override suspend fun tick() {
                val now = OffsetDateTime.now()

                lastUsage.asMap().forEach { (uuid, expireTime) ->
                    if (expireTime.isBefore(now)) {
                        lastUsage.invalidate(uuid)

                        server.getPlayer(uuid)?.sendText {
                            appendSuccessPrefix()
                            success("Der Hole Digger ist wieder bereit!")
                        }
                    }
                }
            }
        }

        @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
        fun onInteract(event: PlayerInteractEvent) {
            lastBlockBlockFace.put(event.player.uniqueId, event.blockFace)
        }

//        @EventHandler
//        fun onBreakTest(event: BlockBreakEvent) {
//            val radius = 10
//            val spawnLocation = event.player.location.world.spawnLocation.clone().apply {
//                x = 0.0
//                z = 0.0
//                y = 100.0
//            }
//            val blockLocation = event.block.location
//
//            if (blockLocation.distanceSquared(spawnLocation) < radius * radius) {
//                event.isCancelled = true
//            }
//        }

        @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
        fun onBreak(event: BlockBreakEvent) {
            if (blockHandler.shouldAvoid(event)) return

            val player = event.player
            val item = player.inventory.itemInMainHand
            if (!item.hasThisEnchantment()) return

            val size = CUBE_SIZE[item.type] ?: 3
            val telekinesis = item.hasEnchantment(TelekinesisEnchantment)

            if (!event.checkCache()) return

            val blockFace = lastBlockBlockFace.getIfPresent(player.uniqueId) ?: return
            val calculatedBlocks = BlockCalculator.calculateBlocks(event.block, blockFace, size / 2)
            val blockResult = blockHandler.handleBlocks(player, calculatedBlocks)

            val blocks = blockResult.breakableBlocks.take(item.calculateDurability()).toMutableList()

            if (blocks.isEmpty()) return

            player.applyCache(blocks.sumOf { ceil(it.type.hardness).toInt() } / 20)
            event.applyItemDamage(blocks)

            val drops = blocks.flatMap { it.getDrops(item) }
            var totalExp = event.expToDrop

            for (breakEvent in blockResult.events) {
                if (breakEvent.isCancelled) continue

                val block = breakEvent.block

                block.type = Material.AIR
                totalExp += breakEvent.expToDrop
            }

            if (telekinesis) {
                event.handleTelekinesis(drops, totalExp)
            } else {
                event.handleNormalDrops(drops, totalExp)
            }
        }

        private fun BlockBreakEvent.checkCache(): Boolean {
            if (lastUsage.getIfPresent(player.uniqueId) != null) {
                if (lastMessage.getIfPresent(player.uniqueId) == null) {
                    player.sendText {
                        appendErrorPrefix()
                        error("Der Hole Digger lädt noch auf!")
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

        private fun Player.applyCache(cooldown: Int) {
            lastUsage.put(uniqueId, OffsetDateTime.now().plusSeconds(cooldown.toLong()))
        }
    }
}