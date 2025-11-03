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
        surfBukkitPacketApi.registerPacketLoreListenerGlobal(this, SurfEnchantmentPacketLoreHandler)

        surfEnchantmentCommand()
    }
}

val plugin get() = JavaPlugin.getPlugin(SurfEnchantment::class.java)