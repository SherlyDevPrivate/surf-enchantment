@file:Suppress("UnstableApiUsage")

package dev.slne.surf.enchantment.enchantments

import com.destroystokyo.paper.event.player.PlayerPostRespawnEvent
import dev.slne.surf.enchantment.enchantment.CustomEnchantment
import dev.slne.surf.enchantment.utils.CustomItemTypeTags
import dev.slne.surf.enchantment.utils.EnchantmentRarity
import dev.slne.surf.surfapi.core.api.messages.adventure.key
import dev.slne.surf.surfapi.core.api.messages.adventure.text
import dev.slne.surf.surfapi.core.api.util.mutableObjectListOf
import dev.slne.surf.surfapi.core.api.util.objectListOf
import dev.slne.surf.surfapi.core.api.util.objectSetOf
import io.papermc.paper.registry.keys.tags.EnchantmentTagKeys
import it.unimi.dsi.fastutil.objects.ObjectList
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import org.bukkit.util.io.BukkitObjectInputStream
import org.bukkit.util.io.BukkitObjectOutputStream
import org.yaml.snakeyaml.external.biz.base64Coder.Base64Coder
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream


object SoulboundEnchantment : CustomEnchantment(
    key("surf", "soulbound"),
    text("Soulbund"),
    EnchantmentRarity.LEGENDARY,
    description = {
        line {
            darkSpacer("Behalte das Item auch nach dem Tod")
        }
    },
    supportedItems = CustomItemTypeTags.EVERY_KEY.tagKey,
    tags = objectSetOf(
        EnchantmentTagKeys.IN_ENCHANTING_TABLE, EnchantmentTagKeys.TREASURE
    ),
    listeners = objectSetOf(Handler)
) {
    val soulboundKey = dev.slne.surf.surfapi.bukkit.api.util.key("soulbound")

    object Handler : Listener {
        @EventHandler
        fun onEntityDeath(event: PlayerDeathEvent) {
            val player = event.player
            val leftItems = objectListOf<ItemStack>()

            for (stack in event.drops) {
                if (!checkItemStackHasEnchantment(stack)) {
                    continue
                }

                event.drops.remove(stack)
                leftItems += stack
            }

            player.persistentDataContainer.set(
                soulboundKey, PersistentDataType.STRING, toBase64(leftItems)
            )
        }

        @EventHandler
        fun onRespawn(event: PlayerPostRespawnEvent) {
            val player = event.player
            val soulboundItems = player.persistentDataContainer.get(
                soulboundKey, PersistentDataType.STRING
            ) ?: return

            for (item in toItems(soulboundItems)) {
                if (item.isEmpty) {
                    continue
                }

                val notAdded = player.inventory.addItem(item)

                notAdded.forEach { (_, leftoverItem) ->
                    player.world.dropItem(player.location, leftoverItem).owner = event.player.uniqueId
                }
            }

            player.persistentDataContainer.remove(soulboundKey)
        }
    }

    fun toBase64(items: ObjectList<ItemStack>): String {
        try {
            val outputStream = ByteArrayOutputStream()
            val dataOutput = BukkitObjectOutputStream(outputStream)

            dataOutput.writeInt(items.size)

            for (i in items.indices) {
                dataOutput.writeObject(items[i])
            }


            dataOutput.close()
            return Base64Coder.encodeLines(outputStream.toByteArray())
        } catch (e: Exception) {
            error("Failed to serialize ItemStack array to Base64: ${e.message}")
        }
    }

    fun toItems(data: String): ObjectList<ItemStack> {
        try {
            val inputStream = ByteArrayInputStream(Base64Coder.decodeLines(data))
            val dataInput = BukkitObjectInputStream(inputStream)
            val items = mutableObjectListOf<ItemStack>(dataInput.readInt())

            for (i in items.indices) {
                items[i] = dataInput.readObject() as ItemStack
            }

            dataInput.close()
            return items
        } catch (e: Exception) {
            error("Failed to deserialize ItemStack array from Base64: ${e.message}")
        }
    }
}