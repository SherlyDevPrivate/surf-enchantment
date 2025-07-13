@file:Suppress("UnstableApiUsage")

package dev.slne.surf.enchantment.enchantment

import dev.slne.surf.enchantment.utils.Enchantable
import dev.slne.surf.surfapi.bukkit.api.builder.LoreBuilder
import dev.slne.surf.surfapi.core.api.util.objectSetOf
import io.papermc.paper.registry.RegistryAccess
import io.papermc.paper.registry.RegistryKey
import io.papermc.paper.registry.TypedKey
import io.papermc.paper.registry.data.EnchantmentRegistryEntry
import io.papermc.paper.registry.tag.TagKey
import it.unimi.dsi.fastutil.objects.ObjectSet
import net.kyori.adventure.key.Key
import net.kyori.adventure.text.Component
import org.bukkit.enchantments.Enchantment
import org.bukkit.event.Listener
import org.bukkit.inventory.EquipmentSlotGroup
import org.bukkit.inventory.ItemRarity
import org.bukkit.inventory.ItemType
import org.jetbrains.annotations.Range

abstract class CustomEnchantment(
    override val key: Key,
    override val displayName: Component,
    override val rarity: ItemRarity,
    override val description: LoreBuilder.(Int) -> Unit = {},

    val supportedItems: TagKey<ItemType>? = null,
    val primaryItems: TagKey<ItemType>? = null,
    val weight: @Range(from = 1, to = 1024) Int? = 1,
    val maxLevel: @Range(from = 1, to = 255) Int? = 1,
    val minimumCost: EnchantmentRegistryEntry.EnchantmentCost? = EnchantmentRegistryEntry.EnchantmentCost.of(
        0, 0
    ),
    val maximumCost: EnchantmentRegistryEntry.EnchantmentCost? = EnchantmentRegistryEntry.EnchantmentCost.of(
        0, 0
    ),
    val anvilCost: @Range(from = 1, to = Int.MAX_VALUE.toLong()) Int? = 1,
    val activeSlots: ObjectSet<EquipmentSlotGroup>? = objectSetOf(
        EquipmentSlotGroup.ANY
    ),
    val exclusiveWith: ObjectSet<Key>? = null,
    val tags: ObjectSet<TagKey<Enchantment>>? = objectSetOf(),
    val typedKey: TypedKey<Enchantment>? = TypedKey.create(RegistryKey.ENCHANTMENT, key),
    val listeners: ObjectSet<Listener> = objectSetOf()
) : Enchantable {
    val bukkitEnchantment by lazy {
        RegistryAccess.registryAccess().getRegistry(RegistryKey.ENCHANTMENT).getOrThrow(key)
    }
}