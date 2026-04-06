@file:Suppress("UnstableApiUsage", "HasPlatformType")

package dev.slne.surf.enchantment.paper.bootstrap

import dev.slne.surf.api.core.util.objectSetOf
import dev.slne.surf.enchantment.api.enchantment.EnchantmentManager
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents
import io.papermc.paper.registry.RegistryKey

fun tagPostFlattenHandler() =
    LifecycleEvents.TAGS.postFlatten(RegistryKey.ENCHANTMENT).newHandler { event ->
        val registrar = event.registrar()
        for (enchantment in EnchantmentManager.customEnchantments) {
            val tags = enchantment.tags ?: continue

            for (tag in tags) {
                registrar.addToTag(tag, objectSetOf(enchantment.typedKey))
            }
        }
    }