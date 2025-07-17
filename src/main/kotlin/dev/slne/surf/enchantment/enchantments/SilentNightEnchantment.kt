@file:Suppress("UnstableApiUsage")

package dev.slne.surf.enchantment.enchantments

import com.destroystokyo.paper.event.entity.PhantomPreSpawnEvent
import dev.slne.surf.enchantment.SurfEnchantment
import dev.slne.surf.enchantment.enchantment.CustomEnchantment
import dev.slne.surf.enchantment.utils.CustomItemTypeTags
import dev.slne.surf.enchantment.utils.EnchantmentRarity
import dev.slne.surf.surfapi.core.api.messages.adventure.key
import dev.slne.surf.surfapi.core.api.messages.adventure.text
import dev.slne.surf.surfapi.core.api.util.objectSetOf
import io.papermc.paper.registry.keys.tags.EnchantmentTagKeys
import it.unimi.dsi.fastutil.objects.ObjectSet
import org.bukkit.enchantments.Enchantment
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener

object SilentNightEnchantment : CustomEnchantment(
    key("surf", "silent_night"),
    text("Silent Night"),
    EnchantmentRarity.LEGENDARY,
    description = {
        line {
            darkSpacer("Lässt den Kopf des Opfers fallen")
        }
    },
    supportedItems = CustomItemTypeTags.HELMET_KEY.tagKey,
    tags = objectSetOf(
        EnchantmentTagKeys.IN_ENCHANTING_TABLE, EnchantmentTagKeys.TREASURE
    ),
    exclusiveWith = ObjectSet.of(Enchantment.LOOTING.key()),
    maxLevel = 3,
    listeners = objectSetOf(Handler)
) {
    object Handler : Listener {
        @EventHandler
        fun onEntitySpawn(event: PhantomPreSpawnEvent) {

        }
    }
}