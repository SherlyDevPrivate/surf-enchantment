@file:Suppress("UnstableApiUsage")

package dev.slne.surf.enchantment.paper.lore

import dev.slne.surf.api.core.util.mutableObject2IntMapOf
import dev.slne.surf.api.paper.packet.lore.SurfPaperPacketLoreHandler
import dev.slne.surf.api.paper.packet.lore.SurfPaperPacketLorePriority
import dev.slne.surf.enchantment.api.enchantment.EnchantmentManager
import dev.slne.surf.enchantment.paper.utils.VanillaEnchantmentMap
import io.papermc.paper.datacomponent.DataComponentTypes
import io.papermc.paper.persistence.PersistentDataContainerView
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack

internal object SurfEnchantmentPacketLoreHandler : SurfPaperPacketLoreHandler {
    override val priority = SurfPaperPacketLorePriority.FIRST

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

        val shouldDisplayEnchantmentDescription = enchantments.size < 5

        enchantments.object2IntEntrySet()
            .mapNotNull {
                val enchantment = VanillaEnchantmentMap.getByKey(it.key.key)
                    ?: EnchantmentManager.findByBukkitEnchantment(it.key)
                    ?: return@mapNotNull null

                enchantment to it.intValue
            }
            .sortedBy { PlainTextComponentSerializer.plainText().serialize(it.first.displayName) }
            .reversed()
            .sortedBy { it.first.rarity }
            .reversed()
            .forEach { (enchantment, level) ->
                loreToDisplay.addAll(enchantment.buildLore(level, shouldDisplayEnchantmentDescription))
            }
    }
}
