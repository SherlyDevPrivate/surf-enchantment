@file:Suppress("UnstableApiUsage")

package dev.slne.surf.enchantment.enchantment

import dev.slne.surf.enchantment.utils.Enchantable
import dev.slne.surf.enchantment.utils.EnchantmentRarity
import dev.slne.surf.surfapi.bukkit.api.builder.LoreBuilder
import dev.slne.surf.surfapi.core.api.util.objectSetOf
import io.papermc.paper.datacomponent.DataComponentTypes
import io.papermc.paper.registry.RegistryAccess
import io.papermc.paper.registry.RegistryKey
import io.papermc.paper.registry.TypedKey
import io.papermc.paper.registry.data.EnchantmentRegistryEntry.EnchantmentCost
import io.papermc.paper.registry.tag.TagKey
import it.unimi.dsi.fastutil.objects.ObjectSet
import net.kyori.adventure.key.Key
import net.kyori.adventure.text.Component
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.event.Listener
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.EquipmentSlotGroup
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ItemType
import org.jetbrains.annotations.Range

abstract class CustomEnchantment(
    override val key: Key,
    override val displayName: Component,
    override val rarity: EnchantmentRarity,
    override val description: LoreBuilder.(Int) -> Unit = {},
    val supportedItems: TagKey<ItemType>? = null,
    val primaryItems: TagKey<ItemType>? = null,
    val weight: @Range(from = 1, to = 1024) Int? = 1,
    override val maxLevel: @Range(from = 1, to = 255) Int? = 1,
    val minimumCost: EnchantmentCost? = EnchantmentCost.of(0, 0),
    val maximumCost: EnchantmentCost? = EnchantmentCost.of(0, 0),
    val anvilCost: @Range(from = 1, to = Int.MAX_VALUE.toLong()) Int? = 1,
    val activeSlots: Set<EquipmentSlotGroup>? = objectSetOf(EquipmentSlotGroup.ANY),
    val exclusiveWith: Set<Key>? = null,
    val tags: Set<TagKey<Enchantment>>? = objectSetOf(),
    val typedKey: TypedKey<Enchantment> = TypedKey.create(RegistryKey.ENCHANTMENT, key),
    val listeners: Set<Listener> = objectSetOf()
) : Enchantable {
    val bukkitEnchantment: Enchantment by lazy {
        RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT).getOrThrow(key)
    }

    private val activeEquipmentSlots by lazy {
        if (activeSlots == null) {
            EquipmentSlot.entries.toSet()
        } else {
            EquipmentSlot.entries.filter { slot ->  activeSlots.any {it.test(slot)} }
        }
    }

    fun checkItemStackHasEnchantment(itemStack: ItemStack) =
        itemStack.getData(DataComponentTypes.ENCHANTMENTS)?.enchantments()
            ?.contains(bukkitEnchantment) == true

    fun ItemStack.hasThisEnchantment() = checkItemStackHasEnchantment(this)

    fun hasEnchantmentActive(player: Player): Boolean {
        val equipment = player.equipment
        return activeEquipmentSlots.any { slot ->
            equipment.getItem(slot).hasThisEnchantment()
        }
    }

    fun Player.hasThisEnchantmentActive() = hasEnchantmentActive(this)
}