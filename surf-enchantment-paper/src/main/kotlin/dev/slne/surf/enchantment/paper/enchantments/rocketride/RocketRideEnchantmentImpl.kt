@file:Suppress("UnstableApiUsage")

package dev.slne.surf.enchantment.paper.enchantments.rocketride

import com.google.auto.service.AutoService
import dev.slne.surf.api.core.messages.adventure.key
import dev.slne.surf.api.core.messages.adventure.text
import dev.slne.surf.api.core.rarity.Rarity
import dev.slne.surf.api.core.util.objectSetOf
import dev.slne.surf.enchantment.api.enchantment.AbstractCustomEnchantment
import dev.slne.surf.enchantment.api.enchantments.RocketRideEnchantment
import dev.slne.surf.enchantment.api.utils.CustomItemTypeTags
import dev.slne.surf.enchantment.paper.enchantments.rocketride.listeners.RocketRideBarteringListener
import dev.slne.surf.enchantment.paper.enchantments.rocketride.listeners.RocketRideBoostListener
import io.papermc.paper.registry.data.EnchantmentRegistryEntry

@AutoService(RocketRideEnchantment::class)
class RocketRideEnchantmentImpl : AbstractCustomEnchantment(
    key = key("surf", "rocket_ride"),
    displayName = text("Rocket Ride"),
    rarity = Rarity.EPIC,
    description = {
        line {
            darkSpacer("Boostet den Happy Ghast, wenn du auf ihm eine Rakete zündest.")
        }
        line {
            darkSpacer("Die Booststärke hängt von der Rakete ab.")
        }
    },
    supportedItems = CustomItemTypeTags.ROCKET_RIDE_KEY.tagKey,
    weight = 2,
    minimumCost = EnchantmentRegistryEntry.EnchantmentCost.of(
        15,
        9
    ),
    maximumCost = EnchantmentRegistryEntry.EnchantmentCost.of(
        65,
        9
    ),
    tags = objectSetOf(),
    listeners = objectSetOf(
        RocketRideBoostListener,
        RocketRideBarteringListener
    ),
    jobs = objectSetOf(RocketRideBoostListener.cooldownHandler)
), RocketRideEnchantment {
    companion object {
        const val CHANCE_TO_DROP_ON_BARTER = 0.02
    }
}