@file:Suppress("UnstableApiUsage")

package dev.slne.surf.enchantment.utils

import dev.slne.surf.surfapi.core.api.messages.adventure.key
import dev.slne.surf.surfapi.core.api.util.objectSetOf
import io.papermc.paper.registry.RegistryKey
import io.papermc.paper.registry.keys.ItemTypeKeys
import io.papermc.paper.registry.tag.TagKey
import io.papermc.paper.tag.TagEntry
import io.papermc.paper.tag.TagEntry.valueEntry
import it.unimi.dsi.fastutil.objects.ObjectSet
import net.kyori.adventure.key.Key
import org.bukkit.inventory.ItemType

enum class CustomItemTypeTags(key: Key, private vararg val tags: TagEntry<ItemType>) {
    WOODEN_TOOLS_KEY(
        key("surf", "wooden_tools"),
        valueEntry(ItemTypeKeys.WOODEN_PICKAXE),
        valueEntry(ItemTypeKeys.WOODEN_AXE),
        valueEntry(ItemTypeKeys.WOODEN_SHOVEL),
        valueEntry(ItemTypeKeys.WOODEN_HOE),
    ),

    IRON_TOOLS_KEY(
        key("surf", "iron_tools"),
        valueEntry(ItemTypeKeys.IRON_PICKAXE),
        valueEntry(ItemTypeKeys.IRON_AXE),
        valueEntry(ItemTypeKeys.IRON_SHOVEL),
        valueEntry(ItemTypeKeys.IRON_HOE),
    ),

    GOLD_TOOLS_KEY(
        key("surf", "gold_tools"),
        valueEntry(ItemTypeKeys.GOLDEN_PICKAXE),
        valueEntry(ItemTypeKeys.GOLDEN_AXE),
        valueEntry(ItemTypeKeys.GOLDEN_SHOVEL),
        valueEntry(ItemTypeKeys.GOLDEN_HOE),
    ),

    DIAMOND_TOOLS_KEY(
        key("surf", "diamond_tools"),
        valueEntry(ItemTypeKeys.DIAMOND_PICKAXE),
        valueEntry(ItemTypeKeys.DIAMOND_AXE),
        valueEntry(ItemTypeKeys.DIAMOND_SHOVEL),
        valueEntry(ItemTypeKeys.DIAMOND_HOE),
    ),

    NETHERITE_TOOLS_KEY(
        key("surf", "netherite_tools"),
        valueEntry(ItemTypeKeys.NETHERITE_PICKAXE),
        valueEntry(ItemTypeKeys.NETHERITE_AXE),
        valueEntry(ItemTypeKeys.NETHERITE_SHOVEL),
        valueEntry(ItemTypeKeys.NETHERITE_HOE),
    ),

    TOOLS_KEY(
        key("surf", "tools"),
        *DIAMOND_TOOLS_KEY.tags,
    );

    val tagKey = TagKey.create(RegistryKey.ITEM, key)
    val tagEntries: ObjectSet<TagEntry<ItemType>> = objectSetOf(*tags)
}