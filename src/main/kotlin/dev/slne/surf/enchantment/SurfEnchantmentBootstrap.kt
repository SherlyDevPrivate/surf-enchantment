@file:Suppress("UnstableApiUsage")

package dev.slne.surf.enchantment

import dev.slne.surf.enchantment.bootstrap.enchantmentComposeHandler
import dev.slne.surf.enchantment.bootstrap.tagPostFlattenHandler
import dev.slne.surf.enchantment.bootstrap.tagPreFlattenHandler
import dev.slne.surf.enchantment.enchantment.EnchantmentManager
import io.papermc.paper.plugin.bootstrap.BootstrapContext
import io.papermc.paper.plugin.bootstrap.PluginBootstrap

class SurfEnchantmentBootstrap : PluginBootstrap {
    override fun bootstrap(context: BootstrapContext) {
        EnchantmentManager.registerSelf()

        with(context.lifecycleManager) {
            registerEventHandler(tagPreFlattenHandler())
            registerEventHandler(tagPostFlattenHandler())
            registerEventHandler(enchantmentComposeHandler())
        }
    }
}