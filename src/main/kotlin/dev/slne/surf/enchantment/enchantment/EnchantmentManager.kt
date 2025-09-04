package dev.slne.surf.enchantment.enchantment

import dev.slne.surf.enchantment.enchantments.BeheadingEnchantment
import dev.slne.surf.enchantment.enchantments.ReplenishEnchantment
import dev.slne.surf.enchantment.enchantments.SheepnesisEnchantment
import dev.slne.surf.enchantment.enchantments.SilentGazeEnchantment
import dev.slne.surf.enchantment.enchantments.SilentNightEnchantment
import dev.slne.surf.enchantment.enchantments.SoulboundEnchantment
import dev.slne.surf.enchantment.enchantments.TelekinesisEnchantment
import dev.slne.surf.surfapi.bukkit.api.event.register
import dev.slne.surf.surfapi.core.api.util.freeze
import dev.slne.surf.surfapi.core.api.util.mutableObject2ObjectMapOf
import net.kyori.adventure.key.Key
import org.bukkit.enchantments.Enchantment
import java.util.concurrent.atomic.AtomicBoolean

object EnchantmentManager {
    private val frozen = AtomicBoolean(false)
    private val _enchantments = mutableObject2ObjectMapOf<Key, CustomEnchantment>()
    val enchantments get() = _enchantments.freeze()

    internal fun registerSelf() {
        register(TelekinesisEnchantment)
        register(SheepnesisEnchantment)
        register(ReplenishEnchantment)
        register(SoulboundEnchantment)
        register(SilentNightEnchantment)
        register(SilentGazeEnchantment)
        register(BeheadingEnchantment)
    }

    internal fun registerEnchantmentListeners() {
        enchantments.forEach { enchantment ->
            enchantment.value.listeners.forEach { listener ->
                listener.register()
            }
        }
    }

    internal fun freeze() {
        frozen.set(true)
    }

    fun getByBukkitEnchantment(enchantment: Enchantment) = enchantments[enchantment.key]

    fun register(enchantment: CustomEnchantment) {
        require(!frozen.get()) { "Cannot register enchantments after the registry is frozen." }
        val added = _enchantments.putIfAbsent(enchantment.key, enchantment) == null
        require(added) { "Enchantment with key ${enchantment.key} is already registered." }
    }
}