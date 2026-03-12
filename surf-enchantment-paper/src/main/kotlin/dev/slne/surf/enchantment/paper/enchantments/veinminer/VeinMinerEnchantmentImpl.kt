package dev.slne.surf.enchantment.paper.enchantments.veinminer

import com.google.auto.service.AutoService
import dev.slne.surf.enchantment.api.enchantment.AbstractCustomEnchantment
import dev.slne.surf.enchantment.api.enchantments.VeinMinerEnchantment
import dev.slne.surf.enchantment.api.utils.CustomItemTypeTags
import dev.slne.surf.enchantment.api.utils.EnchantmentRarity
import dev.slne.surf.enchantment.paper.enchantments.veinminer.listeners.VeinMinerListener
import dev.slne.surf.surfapi.core.api.messages.adventure.key
import dev.slne.surf.surfapi.core.api.messages.adventure.text
import io.papermc.paper.registry.keys.tags.EnchantmentTagKeys
import org.bukkit.enchantments.Enchantment

@AutoService(VeinMinerEnchantment::class)
class VeinMinerEnchantmentImpl : AbstractCustomEnchantment(
    key = key("surf", "vein_miner"),
    displayName = text("Vain Miner"),
    rarity = EnchantmentRarity.EPIC,
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
    supportedItems = CustomItemTypeTags.PICKAXES_KEY.tagKey,
    exclusiveWith = setOf(Enchantment.FORTUNE.key()),
    tags = setOf(
        EnchantmentTagKeys.TRADEABLE,
    ),
    listeners = setOf(VeinMinerListener),
    jobs = setOf(VeinMinerListener.VeinMinerJob)
), VeinMinerEnchantment