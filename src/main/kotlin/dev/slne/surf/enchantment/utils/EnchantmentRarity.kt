package dev.slne.surf.enchantment.utils

import dev.slne.surf.surfapi.core.api.messages.Colors
import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor

enum class EnchantmentRarity(
    val displayName: Component,
    val color: TextColor
) {
    COMMON(
        displayName = buildText { spacer("Common") },
        color = Colors.SPACER
    ),
    UNCOMMON(
        displayName = buildText { spacer("Uncommon") },
        color = Colors.SPACER
    ),
    RARE(
        displayName = buildText { success("Rare") },
        color = Colors.SUCCESS
    ),
    EPIC(
        displayName = buildText { info("Epic") },
        color = Colors.INFO
    ),
    LEGENDARY(
        displayName = buildText { warning("Legendary") },
        color = Colors.WARNING
    ),
    MYTHIC(
        displayName = buildText { error("Mythic") },
        color = Colors.ERROR
    );
}