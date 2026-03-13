package dev.slne.surf.enchantment.paper

import com.github.shynixn.mccoroutine.folia.SuspendingJavaPlugin
import dev.slne.surf.enchantment.paper.commands.surfEnchantmentCommand
import dev.slne.surf.enchantment.paper.enchantment.enchantmentManagerImpl
import dev.slne.surf.enchantment.paper.lore.SurfEnchantmentPacketLoreHandler
import dev.slne.surf.surfapi.bukkit.api.packet.surfBukkitPacketApi
import org.bukkit.plugin.java.JavaPlugin

class SurfEnchantment : SuspendingJavaPlugin() {
    override suspend fun onLoadAsync() {
        enchantmentManagerImpl.freeze()
        enchantmentManagerImpl.registerVanillaEnchantments()
    }

    override suspend fun onEnableAsync() {
        enchantmentManagerImpl.registerEnchantmentListeners()
        surfBukkitPacketApi.registerPacketLoreListenerGlobal(this, SurfEnchantmentPacketLoreHandler)
        enchantmentManagerImpl.startEnchantmentJobs()

        surfEnchantmentCommand()
    }

    override suspend fun onDisableAsync() {
        enchantmentManagerImpl.stopEnchantmentJobs()
    }
}

val plugin get() = JavaPlugin.getPlugin(SurfEnchantment::class.java)