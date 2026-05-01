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
    description = { level ->
        val blocksToMine = level.coerceIn(1, MAX_LEVEL) * BLOCKS_PER_LEVEL

        line { darkSpacer("Ermöglicht es dir alle 20 Sekunden") }
        line {
            darkSpacer("bis zu")
            appendSpace()
            variableValue("$blocksToMine Blöcke")
            appendSpace()
            darkSpacer("eines Baumes auf einmal abzubauen.")
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
    maxLevel = MAX_LEVEL,
    listeners = setOf(LumberJackListener),
    jobs = setOf(LumberJackListener.cooldownHandler)
), LumberJackEnchantment {
    companion object {
        const val MAX_LEVEL = 5
        const val BLOCKS_PER_LEVEL = 7
        const val COOLDOWN_MS = 20_000
        const val INCLUDE_LEAVES = false
    }
}