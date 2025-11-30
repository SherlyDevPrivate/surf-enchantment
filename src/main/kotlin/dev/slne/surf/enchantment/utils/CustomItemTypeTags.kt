@file:Suppress("UnstableApiUsage")

package dev.slne.surf.enchantment.utils

import dev.slne.surf.surfapi.core.api.messages.adventure.key
import dev.slne.surf.surfapi.core.api.util.objectSetOf
import io.papermc.paper.registry.RegistryAccess
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

    HOES_KEY(
        key("surf", "hoes"),
        tagEntry(ItemTypeTagKeys.HOES),
    ),

    WOODEN_TOOLS_KEY(
        key("surf", "wooden_tools"),
        valueEntry(ItemTypeKeys.WOODEN_PICKAXE),
        valueEntry(ItemTypeKeys.WOODEN_AXE),
        valueEntry(ItemTypeKeys.WOODEN_SHOVEL),
        valueEntry(ItemTypeKeys.WOODEN_HOE)
    ),

    WOODEN_TOOLS_AND_SWORDS_KEY(
        key("surf", "wooden_tools_and_swords"),
        tagEntry(WOODEN_TOOLS_KEY.tagKey),
        valueEntry(ItemTypeKeys.WOODEN_SWORD)
    ),

    IRON_TOOLS_KEY(
        key("surf", "iron_tools"),
        valueEntry(ItemTypeKeys.IRON_PICKAXE),
        valueEntry(ItemTypeKeys.IRON_AXE),
        valueEntry(ItemTypeKeys.IRON_SHOVEL),
        valueEntry(ItemTypeKeys.IRON_HOE)
    ),

    IRON_TOOLS_AND_SWORDS_KEY(
        key("surf", "iron_tools_and_swords"),
        tagEntry(IRON_TOOLS_KEY.tagKey),
        valueEntry(ItemTypeKeys.IRON_SWORD)
    ),

    GOLD_TOOLS_KEY(
        key("surf", "gold_tools"),
        valueEntry(ItemTypeKeys.GOLDEN_PICKAXE),
        valueEntry(ItemTypeKeys.GOLDEN_AXE),
        valueEntry(ItemTypeKeys.GOLDEN_SHOVEL),
        valueEntry(ItemTypeKeys.GOLDEN_HOE),
    ),

    GOLD_TOOLS_AND_SWORDS_KEY(
        key("surf", "gold_tools_and_swords"),
        tagEntry(GOLD_TOOLS_KEY.tagKey),
        valueEntry(ItemTypeKeys.GOLDEN_SWORD)
    ),

    DIAMOND_TOOLS_KEY(
        key("surf", "diamond_tools"),
        valueEntry(ItemTypeKeys.DIAMOND_PICKAXE),
        valueEntry(ItemTypeKeys.DIAMOND_AXE),
        valueEntry(ItemTypeKeys.DIAMOND_SHOVEL),
        valueEntry(ItemTypeKeys.DIAMOND_HOE),
    ),

    DIAMOND_TOOLS_AND_SWORDS_KEY(
        key("surf", "diamond_tools_and_swords"),
        tagEntry(DIAMOND_TOOLS_KEY.tagKey),
        valueEntry(ItemTypeKeys.DIAMOND_SWORD)
    ),

    NETHERITE_TOOLS_KEY(
        key("surf", "netherite_tools"),
        valueEntry(ItemTypeKeys.NETHERITE_PICKAXE),
        valueEntry(ItemTypeKeys.NETHERITE_AXE),
        valueEntry(ItemTypeKeys.NETHERITE_SHOVEL),
        valueEntry(ItemTypeKeys.NETHERITE_HOE),
    ),

    NETHERITE_TOOLS_AND_SWORDS_KEY(
        key("surf", "netherite_tools_and_swords"),
        tagEntry(NETHERITE_TOOLS_KEY.tagKey),
        valueEntry(ItemTypeKeys.NETHERITE_SWORD)
    ),

    TOOLS_KEY(
        key("surf", "tools"),
        tagEntry(WOODEN_TOOLS_KEY.tagKey),
        tagEntry(IRON_TOOLS_KEY.tagKey),
        tagEntry(GOLD_TOOLS_KEY.tagKey),
        tagEntry(DIAMOND_TOOLS_KEY.tagKey),
        tagEntry(NETHERITE_TOOLS_KEY.tagKey),
    ),

    LEATHER_ARMOR_KEY(
        key("surf", "leather_armor"),
        valueEntry(ItemTypeKeys.LEATHER_HELMET),
        valueEntry(ItemTypeKeys.LEATHER_CHESTPLATE),
        valueEntry(ItemTypeKeys.LEATHER_LEGGINGS),
        valueEntry(ItemTypeKeys.LEATHER_BOOTS),
    ),

    CHAIN_ARMOR_KEY(
        key("surf", "chain_armor"),
        valueEntry(ItemTypeKeys.CHAINMAIL_HELMET),
        valueEntry(ItemTypeKeys.CHAINMAIL_CHESTPLATE),
        valueEntry(ItemTypeKeys.CHAINMAIL_LEGGINGS),
        valueEntry(ItemTypeKeys.CHAINMAIL_BOOTS),
    ),

    IRON_ARMOR_KEY(
        key("surf", "iron_armor"),
        valueEntry(ItemTypeKeys.IRON_HELMET),
        valueEntry(ItemTypeKeys.IRON_CHESTPLATE),
        valueEntry(ItemTypeKeys.IRON_LEGGINGS),
        valueEntry(ItemTypeKeys.IRON_BOOTS),
    ),

    GOLD_ARMOR_KEY(
        key("surf", "gold_armor"),
        valueEntry(ItemTypeKeys.GOLDEN_HELMET),
        valueEntry(ItemTypeKeys.GOLDEN_CHESTPLATE),
        valueEntry(ItemTypeKeys.GOLDEN_LEGGINGS),
        valueEntry(ItemTypeKeys.GOLDEN_BOOTS),
    ),

    DIAMOND_ARMOR_KEY(
        key("surf", "diamond_armor"),
        valueEntry(ItemTypeKeys.DIAMOND_HELMET),
        valueEntry(ItemTypeKeys.DIAMOND_CHESTPLATE),
        valueEntry(ItemTypeKeys.DIAMOND_LEGGINGS),
        valueEntry(ItemTypeKeys.DIAMOND_BOOTS),
    ),

    NETHERITE_ARMOR_KEY(
        key("surf", "netherite_armor"),
        valueEntry(ItemTypeKeys.NETHERITE_HELMET),
        valueEntry(ItemTypeKeys.NETHERITE_CHESTPLATE),
        valueEntry(ItemTypeKeys.NETHERITE_LEGGINGS),
        valueEntry(ItemTypeKeys.NETHERITE_BOOTS),
    ),

    OTHER_TOOLS_KEY(
        key("surf", "other_tools"),
        valueEntry(ItemTypeKeys.MACE),
        valueEntry(ItemTypeKeys.SHIELD),
        valueEntry(ItemTypeKeys.BOW),
        valueEntry(ItemTypeKeys.FISHING_ROD),
        valueEntry(ItemTypeKeys.CROSSBOW),
        valueEntry(ItemTypeKeys.TRIDENT),
        valueEntry(ItemTypeKeys.FLINT_AND_STEEL),
        valueEntry(ItemTypeKeys.BRUSH),
        valueEntry(ItemTypeKeys.ELYTRA),
    ),

    ARMORS_KEY(
        key("surf", "armors"),
        tagEntry(LEATHER_ARMOR_KEY.tagKey),
        tagEntry(CHAIN_ARMOR_KEY.tagKey),
        tagEntry(IRON_ARMOR_KEY.tagKey),
        tagEntry(GOLD_ARMOR_KEY.tagKey),
        tagEntry(DIAMOND_ARMOR_KEY.tagKey),
        tagEntry(NETHERITE_ARMOR_KEY.tagKey),
    ),

    TOOLS_AND_SWORDS_KEY(
        key("surf", "tools_and_swords"),
        tagEntry(WOODEN_TOOLS_AND_SWORDS_KEY.tagKey),
        tagEntry(IRON_TOOLS_AND_SWORDS_KEY.tagKey),
        tagEntry(GOLD_TOOLS_AND_SWORDS_KEY.tagKey),
        tagEntry(DIAMOND_TOOLS_AND_SWORDS_KEY.tagKey),
        tagEntry(NETHERITE_TOOLS_AND_SWORDS_KEY.tagKey),
    ),

    EVERY_KEY(
        key("surf", "every"),
        tagEntry(TOOLS_AND_SWORDS_KEY.tagKey),
        tagEntry(ARMORS_KEY.tagKey),
        tagEntry(SHEARS_KEY.tagKey),
        tagEntry(OTHER_TOOLS_KEY.tagKey),
    ),

    TOOLS_AND_WEAPONS_KEY(
        key("surf", "tools_and_weapons"),
        tagEntry(ItemTypeTagKeys.BREAKS_DECORATED_POTS),
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