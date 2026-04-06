@file:Suppress("UnstableApiUsage")

package dev.slne.surf.enchantment.paper.enchantments.veinminer

import dev.slne.surf.enchantment.api.enchantment.AbstractCustomEnchantment
import dev.slne.surf.enchantment.api.enchantments.VeinMinerEnchantment
import dev.slne.surf.enchantment.api.utils.CustomItemTypeTags
import dev.slne.surf.enchantment.paper.enchantments.veinminer.listeners.VeinMinerListener
import dev.slne.surf.surfapi.core.api.messages.adventure.key
import dev.slne.surf.surfapi.core.api.messages.adventure.text
import dev.slne.surf.surfapi.core.api.rarity.Rarity
import io.papermc.paper.registry.data.EnchantmentRegistryEntry
import io.papermc.paper.registry.keys.tags.EnchantmentTagKeys
import org.bukkit.enchantments.Enchantment

//@AutoService(VeinMinerEnchantment::class)
class VeinMinerEnchantmentImpl : AbstractCustomEnchantment(
    key = key("surf", "vein_miner"),
    displayName = text("Vain Miner"),
    rarity = Rarity.EPIC,
    description = {
        line { darkSpacer("Erzadern werden vollständig abgebaut") }
        line {
            darkSpacer("Es werden maximal")
            appendSpace()
            variableValue("10 Blöcke")
            appendSpace()
            darkSpacer("pro Nutzung abgebaut.")
        }
        line {
            darkSpacer("Die Abklinzeit beträgt")
            appendSpace()
            variableValue("2 Sekunden")
            appendSpace()
            darkSpacer("pro abgebautem Block.")
        }
    },
    supportedItems = CustomItemTypeTags.VEIN_MINER_KEY.tagKey,
    weight = 2,
    minimumCost = EnchantmentRegistryEntry.EnchantmentCost.of(
        15,
        9
    ),
    maximumCost = EnchantmentRegistryEntry.EnchantmentCost.of(
        65,
        9
    ),
    exclusiveWith = setOf(Enchantment.FORTUNE.key()),
    tags = setOf(
        EnchantmentTagKeys.ON_RANDOM_LOOT,
        EnchantmentTagKeys.IN_ENCHANTING_TABLE,
    ),
    listeners = setOf(VeinMinerListener),
    jobs = setOf(VeinMinerListener.cooldownHandler)
), VeinMinerEnchantment {
    companion object {
        const val MAX_ORES_TO_MINE = 10
    }
}