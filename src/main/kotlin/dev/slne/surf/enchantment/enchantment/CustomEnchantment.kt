@file:Suppress("UnstableApiUsage")

package dev.slne.surf.enchantment.enchantment

import dev.slne.surf.enchantment.utils.Enchantable
import dev.slne.surf.enchantment.utils.EnchantmentRarity
import dev.slne.surf.surfapi.bukkit.api.builder.LoreBuilder
import dev.slne.surf.surfapi.core.api.util.objectSetOf
import io.papermc.paper.registry.RegistryAccess
import io.papermc.paper.registry.RegistryKey
import io.papermc.paper.registry.TypedKey
import io.papermc.paper.registry.data.EnchantmentRegistryEntry.EnchantmentCost
import io.papermc.paper.registry.tag.TagKey
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
    val activeSlots: Set<EquipmentSlotGroup> = objectSetOf(EquipmentSlotGroup.ANY),
    val exclusiveWith: Set<Key>? = null,
    val tags: Set<TagKey<Enchantment>>? = objectSetOf(),
    val typedKey: TypedKey<Enchantment> = TypedKey.create(RegistryKey.ENCHANTMENT, key),
    val listeners: Set<Listener> = objectSetOf()
) : Enchantable {
    val bukkitEnchantment: Enchantment by lazy {
        RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT).getOrThrow(key)
    }

    private val activeEquipmentSlots by lazy {
        EquipmentSlot.entries.filter { slot ->  activeSlots.any {it.test(slot)} }
    }

    fun getThisEnchantmentOrNull(itemStack: ItemStack): Pair<Int, Enchantment>? {
        val enchantments = itemStack.enchantments
        val level = enchantments[bukkitEnchantment] ?: return null
        return Pair(level, bukkitEnchantment)
    }

    fun checkItemStackHasEnchantment(itemStack: ItemStack) = getThisEnchantmentOrNull(itemStack) != null

    fun ItemStack.hasThisEnchantment() = checkItemStackHasEnchantment(this)
    fun ItemStack.getThisEnchantmentOrNull() = getThisEnchantmentOrNull(this)

    fun getThisActiveEnchantmentOrNull(player: Player): Pair<Int, Enchantment>? {
        val equipment = player.equipment
        activeEquipmentSlots.forEach { slot ->
            val item = equipment.getItem(slot)
            val enchantment = getThisEnchantmentOrNull(item)
            if (enchantment != null) return enchantment
        }
        return null
    }

    fun hasEnchantmentActive(player: Player): Boolean {
        return getThisActiveEnchantmentOrNull(player) != null
    }

    fun Player.hasThisEnchantmentActive() = hasEnchantmentActive(this)
    fun Player.getThisActiveEnchantmentOrNull() = getThisActiveEnchantmentOrNull(this)
}