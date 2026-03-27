@file:Suppress("UnstableApiUsage")

package dev.slne.surf.enchantment.api.utils

import dev.slne.surf.surfapi.core.api.messages.adventure.key
import dev.slne.surf.surfapi.core.api.util.objectSetOf
import io.papermc.paper.registry.RegistryKey
import io.papermc.paper.registry.keys.ItemTypeKeys
import io.papermc.paper.registry.keys.tags.ItemTypeTagKeys
import io.papermc.paper.registry.tag.TagKey
import io.papermc.paper.tag.TagEntry
import it.unimi.dsi.fastutil.objects.ObjectSet
import net.kyori.adventure.key.Key
import org.bukkit.inventory.ItemType

enum class CustomItemTypeTags(
    key: Key,
    vararg tags: TagEntry<ItemType>
) {
    SHEARS_KEY(
        key("surf", "shears"),
        TagEntry.valueEntry(ItemTypeKeys.SHEARS)
    ),

    TOOLS_AND_SWORDS_KEY(
        key("surf", "tools_and_swords"),
        TagEntry.tagEntry(ItemTypeTagKeys.AXES),
        TagEntry.tagEntry(ItemTypeTagKeys.HOES),
        TagEntry.tagEntry(ItemTypeTagKeys.PICKAXES),
        TagEntry.tagEntry(ItemTypeTagKeys.SHOVELS),
        TagEntry.tagEntry(ItemTypeTagKeys.SWORDS),
    ),

    TOOLS_AND_WEAPONS_KEY(
        key("surf", "tools_and_weapons"),
        TagEntry.tagEntry(TOOLS_AND_SWORDS_KEY.tagKey),
        TagEntry.tagEntry(ItemTypeTagKeys.ENCHANTABLE_WEAPON)
    ),

    TOOLS_AND_ARMOR_AND_EQUIPMENT_KEY(
        key("surf", "tools_and_armor_and_equipment"),
        TagEntry.tagEntry(ItemTypeTagKeys.BREAKS_DECORATED_POTS), // tools
        TagEntry.tagEntry(ItemTypeTagKeys.ENCHANTABLE_ARMOR), // armor
        TagEntry.tagEntry(ItemTypeTagKeys.ENCHANTABLE_EQUIPPABLE),
        TagEntry.tagEntry(ItemTypeTagKeys.ENCHANTABLE_WEAPON)
    ),
    TOOLS_AND_ARMOR_AND_EQUIPMENT_ESSENTIALS_KEY(
        key("surf", "tools_and_armor_and_equipment_and_essentials"),
        TagEntry.tagEntry(ItemTypeTagKeys.BREAKS_DECORATED_POTS), // tools
        TagEntry.tagEntry(ItemTypeTagKeys.ENCHANTABLE_ARMOR), // armor
        TagEntry.tagEntry(ItemTypeTagKeys.ENCHANTABLE_EQUIPPABLE),
        TagEntry.tagEntry(ItemTypeTagKeys.ENCHANTABLE_WEAPON),
        TagEntry.tagEntry(ItemTypeTagKeys.BUNDLES),
        TagEntry.tagEntry(ItemTypeTagKeys.ENCHANTABLE_FISHING)
    ),

    BUNDLE_KEY(
        key("surf", "bundles"),
        TagEntry.valueEntry(ItemTypeKeys.BUNDLE),
        TagEntry.valueEntry(ItemTypeKeys.WHITE_BUNDLE),
        TagEntry.valueEntry(ItemTypeKeys.ORANGE_BUNDLE),
        TagEntry.valueEntry(ItemTypeKeys.MAGENTA_BUNDLE),
        TagEntry.valueEntry(ItemTypeKeys.LIGHT_BLUE_BUNDLE),
        TagEntry.valueEntry(ItemTypeKeys.YELLOW_BUNDLE),
        TagEntry.valueEntry(ItemTypeKeys.LIME_BUNDLE),
        TagEntry.valueEntry(ItemTypeKeys.PINK_BUNDLE),
        TagEntry.valueEntry(ItemTypeKeys.GRAY_BUNDLE),
        TagEntry.valueEntry(ItemTypeKeys.LIGHT_GRAY_BUNDLE),
        TagEntry.valueEntry(ItemTypeKeys.CYAN_BUNDLE),
        TagEntry.valueEntry(ItemTypeKeys.PURPLE_BUNDLE),
        TagEntry.valueEntry(ItemTypeKeys.BLUE_BUNDLE),
        TagEntry.valueEntry(ItemTypeKeys.BROWN_BUNDLE),
        TagEntry.valueEntry(ItemTypeKeys.GREEN_BUNDLE),
        TagEntry.valueEntry(ItemTypeKeys.RED_BUNDLE),
        TagEntry.valueEntry(ItemTypeKeys.BLACK_BUNDLE)
    ),

    ELYTRA_KEY(
        key("surf", "elytra"),
        TagEntry.valueEntry(ItemTypeKeys.ELYTRA)
    ),

    PICKAXES_KEY(
        key("surf", "pickaxes"),
        TagEntry.valueEntry(ItemTypeKeys.WOODEN_PICKAXE),
        TagEntry.valueEntry(ItemTypeKeys.STONE_PICKAXE),
        TagEntry.valueEntry(ItemTypeKeys.COPPER_PICKAXE),
        TagEntry.valueEntry(ItemTypeKeys.IRON_PICKAXE),
        TagEntry.valueEntry(ItemTypeKeys.GOLDEN_PICKAXE),
        TagEntry.valueEntry(ItemTypeKeys.DIAMOND_PICKAXE),
        TagEntry.valueEntry(ItemTypeKeys.NETHERITE_PICKAXE)
    );

    val tagKey = TagKey.create(RegistryKey.ITEM, key)
    val tagEntries: ObjectSet<TagEntry<ItemType>> = objectSetOf(*tags)
}