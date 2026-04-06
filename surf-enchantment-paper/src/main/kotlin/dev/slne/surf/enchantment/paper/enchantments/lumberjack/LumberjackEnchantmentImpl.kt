package dev.slne.surf.enchantment.paper.enchantments.lumberjack

import com.google.auto.service.AutoService
import dev.slne.surf.enchantment.api.enchantment.AbstractCustomEnchantment
import dev.slne.surf.enchantment.api.enchantments.LumberJackEnchantment
import dev.slne.surf.enchantment.api.utils.CustomItemTypeTags
import dev.slne.surf.enchantment.paper.enchantments.lumberjack.listeners.LumberJackListener
import dev.slne.surf.surfapi.core.api.messages.adventure.key
import dev.slne.surf.surfapi.core.api.messages.adventure.text
import dev.slne.surf.surfapi.core.api.rarity.Rarity
import io.papermc.paper.registry.keys.tags.EnchantmentTagKeys
import org.bukkit.enchantments.Enchantment

@AutoService(LumberJackEnchantment::class)
class LumberjackEnchantmentImpl : AbstractCustomEnchantment(
    key = key("surf", "lumberjack"),
    displayName = text("Lumberjack"),
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
    supportedItems = CustomItemTypeTags.LUMBERJACK_KEY.tagKey,
    exclusiveWith = setOf(Enchantment.FORTUNE.key()),
    tags = setOf(
        EnchantmentTagKeys.IN_ENCHANTING_TABLE,
    ),
    listeners = setOf(LumberJackListener),
    jobs = setOf(LumberJackListener.cooldownHandler)
), LumberJackEnchantment {
    companion object {
        const val MAX_BLOCKS_TO_MINE = 120
        const val INCLUDE_LEAVES = false
    }
}