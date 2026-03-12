package dev.slne.surf.enchantment.paper.enchantment

import com.google.auto.service.AutoService
import dev.slne.surf.enchantment.api.enchantment.CustomEnchantment
import dev.slne.surf.enchantment.api.enchantment.EnchantmentManager
import dev.slne.surf.enchantment.api.enchantment.VanillaEnchantment
import dev.slne.surf.enchantment.api.utils.Enchantable
import dev.slne.surf.enchantment.api.utils.InternalEnchantmentApi
import dev.slne.surf.enchantment.paper.enchantments.beheading.BeheadingEnchantmentImpl
import dev.slne.surf.enchantment.paper.enchantments.experience.ExperienceEnchantmentImpl
import dev.slne.surf.enchantment.paper.enchantments.happyghastboost.HappyGhastBoostEnchantmentImpl
import dev.slne.surf.enchantment.paper.enchantments.replenish.ReplenishEnchantmentImpl
import dev.slne.surf.enchantment.paper.enchantments.rocketsaver.RocketSaverEnchantmentImpl
import dev.slne.surf.enchantment.paper.enchantments.silentgaze.SilentGazeEnchantmentImpl
import dev.slne.surf.enchantment.paper.enchantments.silentnight.SilentNightEnchantmentImpl
import dev.slne.surf.enchantment.paper.enchantments.soulbound.SoulboundEnchantmentImpl
import dev.slne.surf.enchantment.paper.enchantments.telekinesis.TelekinesisEnchantmentImpl
import dev.slne.surf.enchantment.paper.utils.VanillaEnchantmentMap
import dev.slne.surf.surfapi.bukkit.api.event.register
import dev.slne.surf.surfapi.core.api.util.freeze
import dev.slne.surf.surfapi.core.api.util.mutableObjectSetOf
import io.papermc.paper.registry.TypedKey
import net.kyori.adventure.key.Key
import org.bukkit.enchantments.Enchantment
import java.util.concurrent.atomic.AtomicBoolean
import kotlin.reflect.KClass

@AutoService(EnchantmentManager::class)
class EnchantmentManagerImpl : EnchantmentManager {
    private val frozen = AtomicBoolean(false)

    private val _customEnchantments = mutableObjectSetOf<CustomEnchantment>()
    override val customEnchantments = _customEnchantments.freeze()

    private val _vanillaEnchantments = mutableObjectSetOf<VanillaEnchantment>()
    override val vanillaEnchantments = _vanillaEnchantments.freeze()

    init {
        VanillaEnchantmentMap.entries.forEach { entry ->
            _vanillaEnchantments.add(
                VanillaEnchantment(
                    entry.key,
                    entry.displayName,
                    entry.description,
                    entry.rarity,
                    entry.maxLevel
                )
            )
        }
    }

    @InternalEnchantmentApi
    internal fun registerSelf() {
        register(TelekinesisEnchantmentImpl)
        register(ReplenishEnchantmentImpl)
        register(SoulboundEnchantmentImpl)
        register(SilentNightEnchantmentImpl)
        register(SilentGazeEnchantmentImpl)
        register(BeheadingEnchantmentImpl)
        register(RocketSaverEnchantmentImpl)
        register(HappyGhastBoostEnchantmentImpl)
        register(ExperienceEnchantmentImpl)
    }

    @InternalEnchantmentApi
    internal fun registerEnchantmentListeners() {
        _customEnchantments.forEach { enchantment ->
            enchantment.listeners.forEach { listener ->
                listener.register()
            }
        }
    }

    override fun registerCustomEnchantment(enchantment: CustomEnchantment) {
        _customEnchantments.add(enchantment)
    }

    override fun findCustomEnchantmentByKey(key: Key): CustomEnchantment? =
        _customEnchantments.firstOrNull { it.key == key }

    override fun findCustomEnchantment(clazz: KClass<out CustomEnchantment>) =
        _customEnchantments.firstOrNull { it::class == clazz }

    override fun findVanillaEnchantmentByKey(key: TypedKey<Enchantment>) =
        _vanillaEnchantments.firstOrNull { it.key == key }

    override fun findByBukkitEnchantment(enchantment: Enchantment): Enchantable? {
        val customEnchantment =
            _customEnchantments.firstOrNull { it.bukkitEnchantment == enchantment }
        if (customEnchantment != null) return customEnchantment

        return _vanillaEnchantments.firstOrNull { it.key == enchantment.key }
    }

    internal fun freeze() {
        frozen.set(true)
    }

    fun register(enchantment: CustomEnchantment) {
        require(!frozen.get()) { "Cannot register enchantments after the registry is frozen." }
        val added = _customEnchantments.add(enchantment)
        require(added) { "Enchantment with key ${enchantment.key} is already registered." }
    }
}

val enchantmentManagerImpl get() = EnchantmentManager.INSTANCE as EnchantmentManagerImpl