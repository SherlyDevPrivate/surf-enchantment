@file:Suppress("UnstableApiUsage", "HasPlatformType")

package dev.slne.surf.enchantment.bootstrap

import dev.slne.surf.enchantment.enchantment.EnchantmentManager
import dev.slne.surf.surfapi.core.api.util.objectSetOf
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents
import io.papermc.paper.registry.RegistryKey

fun tagPostFlattenHandler() =
    LifecycleEvents.TAGS.postFlatten(RegistryKey.ENCHANTMENT).newHandler { event ->
        val registrar = event.registrar()
        for ((_, enchantment) in EnchantmentManager.enchantments) {
            val tags = enchantment.tags ?: continue
            for (tag in tags) {
                registrar.addToTag(tag, objectSetOf(enchantment.typedKey))
            }
        }
    }