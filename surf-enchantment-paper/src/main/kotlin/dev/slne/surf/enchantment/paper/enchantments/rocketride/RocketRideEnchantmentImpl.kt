package dev.slne.surf.enchantment.paper.enchantments.rocketride

import com.google.auto.service.AutoService
import dev.slne.surf.enchantment.api.enchantment.AbstractCustomEnchantment
import dev.slne.surf.enchantment.api.enchantments.RocketRideEnchantment
import dev.slne.surf.enchantment.api.utils.EnchantmentRarity
import dev.slne.surf.enchantment.paper.enchantments.rocketride.listeners.RocketRideBoostListener
import dev.slne.surf.surfapi.core.api.messages.adventure.key
import dev.slne.surf.surfapi.core.api.messages.adventure.text
import dev.slne.surf.surfapi.core.api.util.objectSetOf
import io.papermc.paper.registry.keys.tags.EnchantmentTagKeys
import io.papermc.paper.registry.keys.tags.ItemTypeTagKeys

@AutoService(RocketRideEnchantment::class)
class RocketRideEnchantmentImpl : AbstractCustomEnchantment(
    key("surf", "rocket_ride"),
    text("Rocket Ride"),
    EnchantmentRarity.EPIC,
    description = {
        line {
            darkSpacer("Boostet den Happy Ghast, wenn du auf ihm eine Rakete zündest.")
        }
    },
    supportedItems = ItemTypeTagKeys.HARNESSES,
    tags = objectSetOf(
        EnchantmentTagKeys.IN_ENCHANTING_TABLE,
        EnchantmentTagKeys.TREASURE
    ),
    listeners = objectSetOf(RocketRideBoostListener),
    jobs = objectSetOf(RocketRideBoostListener.RocketRideBoostJob)
), RocketRideEnchantment