package dev.slne.surf.enchantment

import com.github.shynixn.mccoroutine.folia.SuspendingJavaPlugin
import dev.slne.surf.enchantment.commands.surfEnchantmentCommand
import dev.slne.surf.enchantment.enchantment.EnchantmentManager
import dev.slne.surf.enchantment.lore.SurfEnchantmentPacketLoreHandler
import dev.slne.surf.surfapi.bukkit.api.packet.surfBukkitPacketApi
import org.bukkit.plugin.java.JavaPlugin

class SurfEnchantment : SuspendingJavaPlugin() {
    override suspend fun onLoadAsync() {
        EnchantmentManager.freeze()
    }

    override suspend fun onEnableAsync() {
        EnchantmentManager.registerEnchantmentListeners()
        EnchantmentManager.startEnchantmentJobs()
        surfBukkitPacketApi.registerPacketLoreListenerGlobal(this, SurfEnchantmentPacketLoreHandler)

        surfEnchantmentCommand()
    }

    override suspend fun onDisableAsync() {
        EnchantmentManager.stopEnchantmentJobs()
    }
}

val plugin get() = JavaPlugin.getPlugin(SurfEnchantment::class.java)