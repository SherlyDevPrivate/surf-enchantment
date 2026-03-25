@file:Suppress("UnstableApiUsage", "HasPlatformType")

package dev.slne.surf.enchantment.paper.bootstrap

import dev.slne.surf.enchantment.api.enchantment.EnchantmentManager
import dev.slne.surf.enchantment.paper.enchantment.enchantmentManagerImpl
import io.papermc.paper.registry.RegistryKey
import io.papermc.paper.registry.TypedKey
import io.papermc.paper.registry.event.RegistryEvents
import io.papermc.paper.registry.keys.EnchantmentKeys
import io.papermc.paper.registry.set.RegistrySet
import net.kyori.adventure.key.Key

fun enchantmentComposeHandler() = RegistryEvents.ENCHANTMENT.compose().newHandler { event ->
    enchantmentManagerImpl.registerCustomEnchantments()
    val registry = event.registry()
    for (enchantment in EnchantmentManager.customEnchantments) {
        registry.register(EnchantmentKeys.create(enchantment.key)) { builder ->
            with(enchantment) {
                builder.description(displayName)
                supportedItems?.forEach { tagKey ->
                    builder.supportedItems(event.getOrCreateTag(tagKey))
                }
                primaryItems?.forEach { tagKey ->
                    builder.primaryItems(event.getOrCreateTag(tagKey))
                }
                weight?.let { builder.weight(it) }
                maxLevel?.let { builder.maxLevel(it) }
                minimumCost?.let { builder.minimumCost(it) }
                maximumCost?.let { builder.maximumCost(it) }
                anvilCost?.let { builder.anvilCost(it) }
                activeSlots?.let { builder.activeSlots(it) }
                exclusiveWith?.let {
                    if (it.isNotEmpty()) {
                        builder.exclusiveWith(convertEnchantmentKeys(it))
                    }
                }
            }
        }
    }
}

private fun convertEnchantmentKeys(keys: Set<Key>) = RegistrySet.keySet(
    RegistryKey.ENCHANTMENT,
    keys.map { TypedKey.create(RegistryKey.ENCHANTMENT, it) }
)