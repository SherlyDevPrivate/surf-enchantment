package dev.slne.surf.enchantment.commands

import dev.jorel.commandapi.kotlindsl.commandAPICommand
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.enchantment.utils.SurfEnchantmentPermissionRegistry
import dev.slne.surf.surfapi.core.api.util.logger
import dev.slne.surf.surfapi.core.api.util.toObjectList
import io.papermc.paper.registry.RegistryAccess
import io.papermc.paper.registry.RegistryKey

fun surfEnchantmentDebugCommand() = commandAPICommand("enchantmentdebug") {
    withPermission(SurfEnchantmentPermissionRegistry.COMMAND_BASE)

    playerExecutor { player, args ->
        val enchantments = RegistryAccess
            .registryAccess()
            .getRegistry(RegistryKey.ENCHANTMENT)
            .toObjectList()

        enchantments.forEach { enchantment ->
            logger().atInfo().log("Enchantment: ${enchantment.key.key()} (${enchantment})")
        }
    }
}