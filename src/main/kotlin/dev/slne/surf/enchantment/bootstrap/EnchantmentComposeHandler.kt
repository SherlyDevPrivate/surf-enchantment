@file:Suppress("UnstableApiUsage", "HasPlatformType")

package dev.slne.surf.enchantment.bootstrap

import dev.slne.surf.enchantment.enchantment.EnchantmentManager
import io.papermc.paper.registry.RegistryKey
import io.papermc.paper.registry.TypedKey
import io.papermc.paper.registry.event.RegistryEvents
import io.papermc.paper.registry.keys.EnchantmentKeys
import io.papermc.paper.registry.set.RegistrySet
import net.kyori.adventure.key.Key

fun enchantmentComposeHandler() = RegistryEvents.ENCHANTMENT.compose().newHandler { event ->
    EnchantmentManager.registerSelf()
    val registry = event.registry()
    for ((key, enchantment) in EnchantmentManager.enchantments) {
        registry.register(EnchantmentKeys.create(key)) { builder ->
            with(enchantment) {
                builder.description(displayName)
                supportedItems?.let { builder.supportedItems(event.getOrCreateTag(supportedItems)) }
                primaryItems?.let { builder.primaryItems(event.getOrCreateTag(primaryItems)) }
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