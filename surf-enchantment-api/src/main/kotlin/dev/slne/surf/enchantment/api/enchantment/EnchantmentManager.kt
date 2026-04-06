package dev.slne.surf.enchantment.api.enchantment

import dev.slne.surf.api.core.util.requiredService
import dev.slne.surf.enchantment.api.utils.Enchantable
import io.papermc.paper.registry.TypedKey
import it.unimi.dsi.fastutil.objects.ObjectSet
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Job
import net.kyori.adventure.key.Key
import org.bukkit.Location
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Entity
import org.jetbrains.annotations.Unmodifiable
import kotlin.coroutines.CoroutineContext
import kotlin.reflect.KClass

private val enchantmentManager = requiredService<EnchantmentManager>()

interface EnchantmentManager {
    val vanillaEnchantments: @Unmodifiable ObjectSet<VanillaEnchantment>
    val customEnchantments: @Unmodifiable ObjectSet<CustomEnchantment>

    fun registerCustomEnchantment(enchantment: CustomEnchantment)
    fun findCustomEnchantmentByKey(key: Key): CustomEnchantment?

    fun findCustomEnchantment(clazz: KClass<out CustomEnchantment>): CustomEnchantment?

    fun findVanillaEnchantmentByKey(key: Key): VanillaEnchantment?

    /**
     * Find a vanilla enchantment by [io.papermc.paper.registry.keys.EnchantmentKeys]
     */
    fun findVanillaEnchantmentByTypedKey(key: TypedKey<Enchantment>): VanillaEnchantment?

    fun findByBukkitEnchantment(enchantment: Enchantment): Enchantable?

    val scope: CoroutineScope
    val globalRegionDispatcher: CoroutineContext
    val mainDispatcher: CoroutineContext
    val asyncDispatcher: CoroutineContext
    val entityDispatcher: (Entity) -> CoroutineContext
    val regionDispatcher: (Location) -> CoroutineContext

    fun launch(
        context: CoroutineContext = mainDispatcher,
        start: CoroutineStart = CoroutineStart.DEFAULT,
        block: suspend CoroutineScope.() -> Unit
    ): Job

    companion object : EnchantmentManager by enchantmentManager {
        val INSTANCE get() = enchantmentManager
    }
}

inline fun <reified E : CustomEnchantment> EnchantmentManager.findCustomEnchantment(): E? =
    findCustomEnchantment(E::class) as? E