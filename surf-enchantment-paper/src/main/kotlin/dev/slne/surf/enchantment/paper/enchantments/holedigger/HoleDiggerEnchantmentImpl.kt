@file:Suppress("UnstableApiUsage")

package dev.slne.surf.enchantment.paper.enchantments.holedigger

import com.google.auto.service.AutoService
import dev.slne.surf.enchantment.api.enchantment.AbstractCustomEnchantment
import dev.slne.surf.enchantment.api.enchantments.HoleDiggerEnchantment
import dev.slne.surf.enchantment.api.enchantments.VeinMinerEnchantment
import dev.slne.surf.enchantment.api.utils.CustomItemTypeTags
import dev.slne.surf.enchantment.paper.enchantments.holedigger.listeners.HoleDiggerListener
import dev.slne.surf.surfapi.core.api.messages.adventure.key
import dev.slne.surf.surfapi.core.api.messages.adventure.text
import dev.slne.surf.surfapi.core.api.rarity.Rarity
import io.papermc.paper.registry.keys.tags.EnchantmentTagKeys
import org.bukkit.enchantments.Enchantment

//@AutoService(HoleDiggerEnchantment::class)
class HoleDiggerEnchantmentImpl : AbstractCustomEnchantment(
    key = key("surf", "hole_digger"),
    displayName = text("Hole Digger"),
    rarity = Rarity.MYTHIC,
    description = {
        line { darkSpacer("Gräbt ein Loch in definierter Größe.") }
    },
    supportedItems = setOf(CustomItemTypeTags.PICKAXES_KEY.tagKey),
    exclusiveWith = setOf(
        Enchantment.FORTUNE.key(),
        VeinMinerEnchantment.key
    ),
    tags = setOf(EnchantmentTagKeys.TREASURE),
    listeners = setOf(HoleDiggerListener),
    jobs = setOf(HoleDiggerListener.cooldownHandler)
), HoleDiggerEnchantment