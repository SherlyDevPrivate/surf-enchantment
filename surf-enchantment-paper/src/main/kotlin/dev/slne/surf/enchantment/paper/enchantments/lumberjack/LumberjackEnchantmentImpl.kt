package dev.slne.surf.enchantment.paper.enchantments.lumberjack

import com.google.auto.service.AutoService
import dev.slne.surf.api.core.messages.adventure.key
import dev.slne.surf.api.core.rarity.Rarity
import dev.slne.surf.enchantment.api.enchantment.AbstractCustomEnchantment
import dev.slne.surf.enchantment.api.enchantments.LumberJackEnchantment
import dev.slne.surf.enchantment.api.utils.CustomItemTypeTags
import dev.slne.surf.enchantment.paper.enchantments.lumberjack.listeners.LumberJackListener
import io.papermc.paper.registry.keys.tags.EnchantmentTagKeys
import net.kyori.adventure.text.Component.text
import org.bukkit.enchantments.Enchantment

@AutoService(LumberJackEnchantment::class)
class LumberjackEnchantmentImpl : AbstractCustomEnchantment(
    key = key("surf", "lumberjack"),
    displayName = text("Lumberjack"),
    rarity = Rarity.EPIC,
    description = {
        line { darkSpacer("Bäume werden vollständig und mit einem Schlag abgebaut") }
        line {
            darkSpacer("Es werden maximal")
            appendSpace()
            variableValue("$MAX_BLOCKS_TO_MINE Blöcke")
            appendSpace()
            darkSpacer("pro Nutzung abgebaut.")
        }
        line {
            darkSpacer("Solltest du den")
            appendSpace()
            variableValue("Woodcutting Skill")
            appendSpace()
            darkSpacer("gelevelt haben, verkürzt sich der Cooldown.")
        }
    },
    supportedItems = CustomItemTypeTags.LUMBERJACK_KEY.tagKey,
    exclusiveWith = setOf(Enchantment.SILK_TOUCH.key()),
    tags = setOf(
        EnchantmentTagKeys.IN_ENCHANTING_TABLE,
    ),
    listeners = setOf(LumberJackListener),
    jobs = setOf(LumberJackListener.cooldownHandler)
), LumberJackEnchantment {
    companion object {
        const val MAX_BLOCKS_TO_MINE = 120
        const val COOLDOWN_PER_BLOCK_MS = 1500
        const val INCLUDE_LEAVES = false
    }
}