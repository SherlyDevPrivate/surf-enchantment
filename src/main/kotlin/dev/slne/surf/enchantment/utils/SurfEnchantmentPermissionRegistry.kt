package dev.slne.surf.enchantment.utils

import dev.slne.surf.surfapi.bukkit.api.permission.PermissionRegistry

internal object SurfEnchantmentPermissionRegistry : PermissionRegistry() {

    private val PREFIX = "surf.enchantment"
    private val COMMAND_PREFIX = "$PREFIX.command"

    val COMMAND_BASE = "$COMMAND_PREFIX.base"

}