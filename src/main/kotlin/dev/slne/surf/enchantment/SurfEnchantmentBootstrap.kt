@file:Suppress("UnstableApiUsage")

package dev.slne.surf.enchantment

import dev.slne.surf.enchantment.bootstrap.enchantmentComposeHandler
import dev.slne.surf.enchantment.bootstrap.tagPostFlattenHandler
import dev.slne.surf.enchantment.bootstrap.tagPreFlattenHandler
import io.papermc.paper.plugin.bootstrap.BootstrapContext
import io.papermc.paper.plugin.bootstrap.PluginBootstrap
import io.papermc.paper.plugin.lifecycle.event.types.LifecycleEvents
import io.papermc.paper.registry.event.RegistryEvents

class SurfEnchantmentBootstrap : PluginBootstrap {
    override fun bootstrap(context: BootstrapContext) {
        with(context.lifecycleManager) {


            registerEventHandler(tagPreFlattenHandler())
            registerEventHandler(tagPostFlattenHandler())
            registerEventHandler(enchantmentComposeHandler())
        }
    }
}