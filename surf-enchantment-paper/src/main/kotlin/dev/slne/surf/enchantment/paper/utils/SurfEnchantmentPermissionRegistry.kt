package dev.slne.surf.enchantment.paper.utils

import dev.slne.surf.enchantment.api.utils.InternalEnchantmentApi
import dev.slne.surf.surfapi.bukkit.api.permission.PermissionRegistry

@InternalEnchantmentApi
internal object SurfEnchantmentPermissionRegistry : PermissionRegistry() {
    private const val PREFIX = "surf.enchantment"
    private const val COMMAND_PREFIX = "$PREFIX.command"

    val COMMAND_BASE = create("$COMMAND_PREFIX.base")
}