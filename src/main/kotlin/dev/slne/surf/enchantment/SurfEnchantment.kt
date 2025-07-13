package dev.slne.surf.enchantment

import com.github.shynixn.mccoroutine.folia.SuspendingJavaPlugin
import dev.slne.surf.enchantment.enchantment.EnchantmentManager
import dev.slne.surf.enchantment.lore.SurfEnchantmentPacketLoreHandler
import dev.slne.surf.surfapi.bukkit.api.packet.surfBukkitPacketApi
import org.bukkit.plugin.java.JavaPlugin

val plugin get() = JavaPlugin.getPlugin(SurfEnchantment::class.java)

class SurfEnchantment : SuspendingJavaPlugin() {

    override suspend fun onLoadAsync() {
        EnchantmentManager.bootstrapped = true
    }

    override suspend fun onEnableAsync() {
        EnchantmentManager.registerEnchantmentListeners()

        surfBukkitPacketApi.registerPacketLoreListenerGlobal(this, SurfEnchantmentPacketLoreHandler)
    }

}