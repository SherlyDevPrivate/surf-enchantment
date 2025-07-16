@file:Suppress("UnstableApiUsage", "HasPlatformType")

package dev.slne.surf.enchantment.bootstrap

import dev.slne.surf.enchantment.utils.CustomItemTypeTags
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents
import io.papermc.paper.registry.RegistryKey

fun tagPreFlattenHandler() = LifecycleEvents.TAGS.preFlatten(RegistryKey.ITEM).newHandler { event ->
    val registrar = event.registrar()
    CustomItemTypeTags.entries.forEach { registrar.addToTag(it.tagKey, it.tagEntries) }
}