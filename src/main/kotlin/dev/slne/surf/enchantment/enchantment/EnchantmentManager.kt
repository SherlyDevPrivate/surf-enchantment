package dev.slne.surf.enchantment.enchantment

import dev.slne.surf.enchantment.enchantments.*
import dev.slne.surf.enchantment.enchantments.holedigger.HoleDiggerEnchantment
import dev.slne.surf.enchantment.enchantments.rocketride.RocketRideEnchantment
import dev.slne.surf.enchantment.enchantments.veinminer.VeinMinerEnchantment
import dev.slne.surf.surfapi.bukkit.api.event.register
import dev.slne.surf.surfapi.core.api.util.freeze
import dev.slne.surf.surfapi.core.api.util.mutableObject2ObjectMapOf
import net.kyori.adventure.key.Key
import org.bukkit.enchantments.Enchantment
import java.util.concurrent.atomic.AtomicBoolean

object EnchantmentManager {
    private val frozen = AtomicBoolean(false)
    private val _enchantments = mutableObject2ObjectMapOf<Key, CustomEnchantment>()
    val enchantments = _enchantments.freeze()

    internal fun registerSelf() {
        register(TelekinesisEnchantment)
        register(ReplenishEnchantment)
        register(SoulboundEnchantment)
        register(SilentNightEnchantment)
        register(SilentGazeEnchantment)
        register(BeheadingEnchantment)
        register(RocketSaverEnchantment)
        register(RocketRideEnchantment)
        register(ExperienceEnchantment)
        register(VeinMinerEnchantment)
        register(HoleDiggerEnchantment)
    }

    internal fun startEnchantmentJobs() {
        enchantments.forEach { enchantment ->
            enchantment.value.jobs.forEach { job ->
                job.start()
            }
        }
    }

    internal fun stopEnchantmentJobs() {
        enchantments.forEach { enchantment ->
            enchantment.value.jobs.forEach { job ->
                job.stop()
            }
        }
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