@file:Suppress("UnstableApiUsage")

package dev.slne.surf.enchantment.lore

import dev.slne.surf.enchantment.enchantment.EnchantmentManager
import dev.slne.surf.enchantment.utils.VanillaEnchantment
import dev.slne.surf.surfapi.bukkit.api.packet.lore.SurfBukkitPacketLoreHandler
import dev.slne.surf.surfapi.core.api.util.mutableObject2IntMapOf
import io.papermc.paper.datacomponent.DataComponentTypes
import io.papermc.paper.persistence.PersistentDataContainerView
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack

object SurfEnchantmentPacketLoreHandler : SurfBukkitPacketLoreHandler {
    override fun handleLore(
        loreToDisplay: MutableList<Component>,
        pdc: PersistentDataContainerView,
        itemStack: ItemStack
    ) {
        val enchantments = mutableObject2IntMapOf<Enchantment>()

        itemStack.getData(DataComponentTypes.ENCHANTMENTS)?.let { data ->
            enchantments.putAll(data.enchantments())
        }

        itemStack.getData(DataComponentTypes.STORED_ENCHANTMENTS)?.let { data ->
            enchantments.putAll(data.enchantments())
        }

        if (enchantments.isEmpty()) return

        itemStack.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_STORED_ENCHANTS)

        val enchantables = enchantments.object2IntEntrySet()
            .mapNotNull {
                val enchantment = VanillaEnchantment.getByKey(it.key.key)
                    ?: EnchantmentManager.getByBukkitEnchantment(it.key) ?: return@mapNotNull null

                enchantment to it.intValue
            }
            .sortedBy { PlainTextComponentSerializer.plainText().serialize(it.first.displayName) }
            .sortedBy { it.first.rarity }

        enchantables.forEachIndexed { index, (enchantment, level) ->
            if (index > 0) {
                loreToDisplay.add(Component.empty())
            }

            loreToDisplay.addAll(enchantment.buildLore(level))
        }
    }
}