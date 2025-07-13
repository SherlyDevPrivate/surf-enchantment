@file:Suppress("UnstableApiUsage")

package dev.slne.surf.enchantment

import dev.slne.surf.enchantment.enchantment.CustomEnchantment
import dev.slne.surf.enchantment.enchantment.EnchantmentManager
import dev.slne.surf.enchantment.utils.CustomItemTypeTags
import dev.slne.surf.surfapi.core.api.util.objectSetOf
import io.papermc.paper.plugin.bootstrap.BootstrapContext
import io.papermc.paper.plugin.bootstrap.PluginBootstrap
import io.papermc.paper.plugin.lifecycle.event.registrar.ReloadableRegistrarEvent
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents
import io.papermc.paper.registry.RegistryKey
import io.papermc.paper.registry.TypedKey
import io.papermc.paper.registry.data.EnchantmentRegistryEntry
import io.papermc.paper.registry.event.RegistryComposeEvent
import io.papermc.paper.registry.event.RegistryEvents
import io.papermc.paper.registry.keys.EnchantmentKeys
import io.papermc.paper.registry.set.RegistrySet
import io.papermc.paper.tag.PostFlattenTagRegistrar
import io.papermc.paper.tag.PreFlattenTagRegistrar
import it.unimi.dsi.fastutil.objects.ObjectSet
import net.kyori.adventure.key.Key
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemType

class SurfEnchantmentBootstrap : PluginBootstrap {

    override fun bootstrap(context: BootstrapContext) {
        EnchantmentManager.registerSelf()

        context.lifecycleManager.registerEventHandler(LifecycleEvents.TAGS.preFlatten(RegistryKey.ITEM)) { event ->
            registerTypeTags(event)
        }

        context.lifecycleManager.registerEventHandler(LifecycleEvents.TAGS.postFlatten(RegistryKey.ENCHANTMENT)) { event ->
            EnchantmentManager.enchantments.forEach {
                registerCustomEnchantmentTags(
                    event, it.value
                )
            }
        }

        context.lifecycleManager.registerEventHandler(
            RegistryEvents.ENCHANTMENT.compose().newHandler { event ->
                EnchantmentManager.enchantments.forEach { registerEnchantment(event, it.value) }
            })
    }

    private fun registerTypeTags(event: ReloadableRegistrarEvent<PreFlattenTagRegistrar<ItemType>>) {
        CustomItemTypeTags.entries.forEach { entry ->
            event.registrar().addToTag(entry.tagKey, entry.tagEntries)
        }
    }

    private fun registerCustomEnchantmentTags(
        event: ReloadableRegistrarEvent<PostFlattenTagRegistrar<Enchantment>>,
        enchantment: CustomEnchantment
    ) {
        enchantment.tags?.forEach { tag ->
            event.registrar().addToTag(tag, objectSetOf(enchantment.typedKey!!))
        }
    }

    private fun registerEnchantment(
        event: RegistryComposeEvent<Enchantment, EnchantmentRegistryEntry.Builder>,
        enchantment: CustomEnchantment
    ) {
        event.registry().register(
            EnchantmentKeys.create(enchantment.key)
        ) { builder ->
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

    private fun convertEnchantmentKeys(keys: ObjectSet<Key>) =
        RegistrySet.keySet(
            RegistryKey.ENCHANTMENT,
            *keys.map { TypedKey.create(RegistryKey.ENCHANTMENT, it) }.toTypedArray()
        )
}