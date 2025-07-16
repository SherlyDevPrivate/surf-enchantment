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

internal object SurfEnchantmentPacketLoreHandler : SurfBukkitPacketLoreHandler {
    override fun handleLore(
        loreToDisplay: MutableList<Component>,
        pdc: PersistentDataContainerView,
        itemStack: ItemStack
    ) {
        val enchantments = mutableObject2IntMapOf<Enchantment>().apply {
            itemStack.getData(DataComponentTypes.ENCHANTMENTS)?.enchantments()?.let { putAll(it) }
            itemStack.getData(DataComponentTypes.STORED_ENCHANTMENTS)?.enchantments()
                ?.let { putAll(it) }
        }

        if (enchantments.isEmpty()) return

        itemStack.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_STORED_ENCHANTS)

        enchantments.object2IntEntrySet().asSequence()
            .mapNotNull {
                val enchantment = VanillaEnchantment.getByKey(it.key.key)
                    ?: EnchantmentManager.getByBukkitEnchantment(it.key) ?: return@mapNotNull null

                enchantment to it.intValue
            }
            .sortedBy { PlainTextComponentSerializer.plainText().serialize(it.first.displayName) }
            .reversed()
            .sortedBy { it.first.rarity }
            .reversed()
            .forEachIndexed { index, (enchantment, level) ->
                if (index > 0) {
                    loreToDisplay.add(Component.empty())
                }

                loreToDisplay.addAll(enchantment.buildLore(level))
            }
    }
}