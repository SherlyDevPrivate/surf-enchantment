package dev.slne.surf.enchantment.paper.commands

import dev.jorel.commandapi.kotlindsl.commandAPICommand
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.api.core.util.toObjectList
import dev.slne.surf.api.paper.builder.ItemStack
import dev.slne.surf.api.paper.builder.displayName
import dev.slne.surf.enchantment.paper.utils.SurfEnchantmentPermissionRegistry
import io.papermc.paper.registry.RegistryAccess
import io.papermc.paper.registry.RegistryKey
import org.bukkit.Material
import org.bukkit.inventory.ItemFlag

fun surfEnchantmentCommand() = commandAPICommand("enchantment") {
    withPermission(SurfEnchantmentPermissionRegistry.COMMAND_BASE)

    playerExecutor { player, args ->
        val enchantments = RegistryAccess
            .registryAccess()
            .getRegistry(RegistryKey.ENCHANTMENT)
            .toObjectList()
            .chunked(10)

        enchantments.forEachIndexed { index, chunk ->
            val item = ItemStack(Material.STICK) {
                displayName {
                    primary("Der Alleskönner-Stab")
                    variableValue(" #$index")
                }

                chunk.forEach { enchantment ->
                    addUnsafeEnchantment(enchantment, enchantment.maxLevel)
                }

                addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
            }

            player.inventory.addItem(item)
        }
    }
}