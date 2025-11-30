@file:Suppress("UnstableApiUsage")

package dev.slne.surf.enchantment.utils

import dev.slne.surf.surfapi.core.api.messages.adventure.key
import dev.slne.surf.surfapi.core.api.util.objectSetOf
import io.papermc.paper.registry.RegistryKey
import io.papermc.paper.registry.keys.ItemTypeKeys
import io.papermc.paper.registry.keys.tags.ItemTypeTagKeys
import io.papermc.paper.registry.tag.TagKey
import io.papermc.paper.tag.TagEntry
import io.papermc.paper.tag.TagEntry.tagEntry
import io.papermc.paper.tag.TagEntry.valueEntry
import it.unimi.dsi.fastutil.objects.ObjectSet
import net.kyori.adventure.key.Key
import org.bukkit.inventory.ItemType

enum class CustomItemTypeTags(key: Key, private vararg val tags: TagEntry<ItemType>) {

    SHEARS_KEY(
        key("surf", "shears"),
        valueEntry(ItemTypeKeys.SHEARS)
    ),

    TOOLS_AND_SWORDS_KEY(
        key("surf", "tools_and_swords"),
        tagEntry(ItemTypeTagKeys.AXES),
        tagEntry(ItemTypeTagKeys.HOES),
        tagEntry(ItemTypeTagKeys.PICKAXES),
        tagEntry(ItemTypeTagKeys.SHOVELS),
        tagEntry(ItemTypeTagKeys.SWORDS),
    ),

    TOOLS_AND_WEAPONS_KEY(
        key("surf", "tools_and_weapons"),
        tagEntry(TOOLS_AND_SWORDS_KEY.tagKey),
        tagEntry(ItemTypeTagKeys.ENCHANTABLE_WEAPON)
    ),

    TOOLS_AND_ARMOR_AND_EQUIPMENT_KEY(
        key("surf", "tools_and_armor_and_equipment"),
        tagEntry(ItemTypeTagKeys.BREAKS_DECORATED_POTS), // tools
        tagEntry(ItemTypeTagKeys.ENCHANTABLE_ARMOR), // armor
        tagEntry(ItemTypeTagKeys.ENCHANTABLE_EQUIPPABLE),
        tagEntry(ItemTypeTagKeys.ENCHANTABLE_WEAPON)
    ),

    ELYTRA_KEY(
        key("surf", "elytra"),
        valueEntry(ItemTypeKeys.ELYTRA)
    );


    val tagKey = TagKey.create(RegistryKey.ITEM, key)
    val tagEntries: ObjectSet<TagEntry<ItemType>> = objectSetOf(*tags)
}