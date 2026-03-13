package dev.slne.surf.enchantment.api.utils

import dev.slne.surf.surfapi.core.api.messages.adventure.buildText
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor

enum class EnchantmentRarity(
    val displayName: Component,
    val color: TextColor
) {
    COMMON( //TODO: Find suitable colors and implement them into surf-api so they also can used in future features  e.g. fishing system
        displayName = buildText { spacer("Gewöhnlich") },
        color = TextColor.color(0xAAAAAA)
    ),
    UNCOMMON(
        displayName = buildText { spacer("Ungewöhnlich") },
        color = TextColor.color(0x55FF55)
    ),
    RARE(
        displayName = buildText { success("Selten") },
        color = TextColor.color(0x55FFFF)
    ),
    EPIC(
        displayName = buildText { info("Episch") },
        color = TextColor.color(0xFF55FF)

    ),
    LEGENDARY(
        displayName = buildText { warning("Legendär") },
        color = TextColor.color(0xFFAA00)
    ),
    MYTHIC(
        displayName = buildText { error("Mythisch") },
        color = TextColor.color(0xAA00AA)
    );
}