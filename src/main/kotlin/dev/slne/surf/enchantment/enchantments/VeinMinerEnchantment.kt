@file:Suppress("UnstableApiUsage")

package dev.slne.surf.enchantment.enchantments

import CoreProtectHook
import com.github.benmanes.caffeine.cache.Caffeine
import dev.slne.surf.enchantment.enchantment.CustomEnchantment
import dev.slne.surf.enchantment.utils.CustomItemTypeTags
import dev.slne.surf.enchantment.utils.EnchantmentRarity
import dev.slne.surf.surfapi.bukkit.api.extensions.server
import dev.slne.surf.surfapi.core.api.messages.adventure.key
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import dev.slne.surf.surfapi.core.api.messages.adventure.text
import io.papermc.paper.registry.keys.tags.EnchantmentTagKeys
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.enchantments.Enchantment
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Vector
import java.util.*
import kotlin.time.Duration.Companion.seconds
import kotlin.time.toJavaDuration

object VeinMinerEnchantment : CustomEnchantment(
    key("surf", "vein_miner"),
    text("Vain Miner"),
    EnchantmentRarity.EPIC,
    description = {
        line { darkSpacer("Erzadern werden vollständig abgebaut") }
        line {
            darkSpacer("Die Abklinzeit beträgt")
            appendSpace()
            variableValue("10 Sekunden")
        }
    },
    supportedItems = CustomItemTypeTags.PICKAXES_KEY.tagKey,
    exclusiveWith = setOf(Enchantment.FORTUNE.key()),
    tags = setOf(
        EnchantmentTagKeys.TRADEABLE,
    ),
    listeners = setOf(Handler)
) {
    object Handler : Listener {

        private val oreTypes = setOf(
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
        private const val MAX_ORES = 15

        val POSSIBLE_CONNECTIONS = listOf(
            Vector(1, 0, 0), Vector(-1, 0, 0),
            Vector(0, 1, 0), Vector(0, -1, 0),
            Vector(0, 0, 1), Vector(0, 0, -1)
        )

        private const val COOLDOWN_SECONDS = 30
        private val lastToolUsage = Caffeine.newBuilder()
            .expireAfterWrite(COOLDOWN_SECONDS.seconds.toJavaDuration())
            .executor(Runnable::run)
            .removalListener<UUID, Unit> { uuid, _, _ ->
                if (uuid != null) {
                    server.getPlayer(uuid)?.sendText {
                        appendSuccessPrefix()
                        success("Der Vein Miners ist nun wieder verwendbar!")
                    } ?: return@removalListener
                }
            }
            .build<UUID, Unit>()

        private val lastMessageSent = Caffeine.newBuilder()
            .expireAfterWrite(10.seconds.toJavaDuration())
            .build<UUID, Unit>()

        @EventHandler
        fun onBreak(event: BlockBreakEvent) {
            val player = event.player
            val item = player.inventory.itemInMainHand

            if (!item.hasThisEnchantment()) return
            val telekinesis = item.hasEnchantment(TelekinesisEnchantment)

            val block = event.block
            if (block.type !in oreTypes) return

            if (lastToolUsage.getIfPresent(player.uniqueId) != null) {
                if (lastMessageSent.getIfPresent(player.uniqueId) != null) return
                player.sendText {
                    appendErrorPrefix()
                    error("Der Vein Miner hat eine Abklingzeit von")
                    appendSpace()
                    variableValue("$COOLDOWN_SECONDS Sekunden")
                    appendSpace()
                    error("pro Werkzeug!")
                }
                lastMessageSent.put(player.uniqueId, Unit)
                return
            }

            val vein = findConnectedOres(block, MAX_ORES)
            if (vein.size <= 1) return

            lastToolUsage.put(player.uniqueId, Unit)

            var durabilityLoss = 0
            var totalExp = 0
            val collectedDrops = mutableListOf<ItemStack>()

            vein.forEach { ore ->
                totalExp += ore.type.hardness.toInt().coerceAtLeast(1)
                collectedDrops += ore.getDrops(item)
                durabilityLoss++
                CoreProtectHook.logRemoval(player.name, ore.location, ore.type)
                ore.type = Material.AIR
            }

            item.damage(durabilityLoss, player)

            if (telekinesis) {
                collectedDrops.forEach { drop ->
                    val leftover = player.inventory.addItem(drop)
                    leftover.values.forEach { player.world.dropItem(player.location, it) }
                }
                player.giveExp(totalExp, true)
                event.expToDrop = 0

            } else {
                val dropLocation = block.location.clone().add(0.5, 0.5, 0.5)
                collectedDrops.forEach { drop ->
                    dropLocation.world?.dropItem(dropLocation, drop)
                }
                event.expToDrop = totalExp
            }
        }

        private fun findConnectedOres(start: Block, max: Int): Set<Block> {
            val result = mutableSetOf<Block>()
            val queue: Queue<Block> = LinkedList()

            queue.add(start)
            result.add(start)

            while (queue.isNotEmpty() && result.size < max) {
                val current = queue.poll()

                for (dir in POSSIBLE_CONNECTIONS) {
                    val next = current.location.clone().add(dir).block
                    if (next.type == start.type && next !in result) {
                        result.add(next)
                        queue.add(next)
                    }
                }
            }

            return result
        }
    }
}