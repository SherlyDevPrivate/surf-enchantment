package dev.slne.surf.enchantment.enchantment

import dev.slne.surf.enchantment.enchantments.ReplenishEnchantment
import dev.slne.surf.enchantment.enchantments.SheepnesisEnchantment
import dev.slne.surf.enchantment.enchantments.TelekinesisEnchantment
import dev.slne.surf.surfapi.bukkit.api.event.register
import dev.slne.surf.surfapi.core.api.util.freeze
import dev.slne.surf.surfapi.core.api.util.mutableObject2ObjectMapOf
import net.kyori.adventure.key.Key
import org.bukkit.enchantments.Enchantment

object EnchantmentManager {

    internal var bootstrapped = false
        set(value) {
            if (field) {
                error("The enchantment registry is already bootstrapped. Cannot change the bootstrapped state.")
            }

            field = value
        }

    private val _enchantments = mutableObject2ObjectMapOf<Key, CustomEnchantment>()
    val enchantments get() = _enchantments.freeze()

    internal fun registerSelf() {
        register(TelekinesisEnchantment)
        register(SheepnesisEnchantment)
        register(ReplenishEnchantment)
    }

    internal fun registerEnchantmentListeners() {
        enchantments.forEach { enchantment ->
            enchantment.value.listeners.forEach { listener ->
                listener.register()
            }
        }
    }

    fun getByBukkitEnchantment(enchantment: Enchantment) =
        enchantments[enchantment.key]

    fun register(enchantment: CustomEnchantment) {
        if (bootstrapped) {
            error("The enchantment registry is locked after the plugin is bootstrapped. Please register all enchantments before the plugin starts.")
        }

        _enchantments.putIfAbsent(enchantment.key, enchantment)
    }
}