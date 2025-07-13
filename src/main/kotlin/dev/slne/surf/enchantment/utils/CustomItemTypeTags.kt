@file:Suppress("UnstableApiUsage")

package dev.slne.surf.enchantment.utils

import dev.slne.surf.surfapi.core.api.util.objectSetOf
import io.papermc.paper.registry.RegistryKey
import io.papermc.paper.registry.keys.ItemTypeKeys
import io.papermc.paper.registry.tag.TagKey
import io.papermc.paper.tag.TagEntry
import it.unimi.dsi.fastutil.objects.ObjectSet
import net.kyori.adventure.key.Key
import org.bukkit.inventory.ItemType

enum class CustomItemTypeTags(
    val tagKey: TagKey<ItemType>,
    val tagEntries: ObjectSet<TagEntry<ItemType>>
) {

    WOODEN_TOOLS_KEY(
        TagKey.create(RegistryKey.ITEM, Key.key("surf", "wooden_tools")),
        objectSetOf(
            TagEntry.valueEntry(ItemTypeKeys.WOODEN_PICKAXE),
            TagEntry.valueEntry(ItemTypeKeys.WOODEN_AXE),
            TagEntry.valueEntry(ItemTypeKeys.WOODEN_SHOVEL),
            TagEntry.valueEntry(ItemTypeKeys.WOODEN_HOE),
        )
    ),

    IRON_TOOLS_KEY(
        TagKey.create(RegistryKey.ITEM, Key.key("surf", "iron_tools")),
        objectSetOf(
            TagEntry.valueEntry(ItemTypeKeys.IRON_PICKAXE),
            TagEntry.valueEntry(ItemTypeKeys.IRON_AXE),
            TagEntry.valueEntry(ItemTypeKeys.IRON_SHOVEL),
            TagEntry.valueEntry(ItemTypeKeys.IRON_HOE),
        )
    ),

    GOLD_TOOLS_KEY(
        TagKey.create(RegistryKey.ITEM, Key.key("surf", "gold_tools")),
        objectSetOf(
            TagEntry.valueEntry(ItemTypeKeys.GOLDEN_PICKAXE),
            TagEntry.valueEntry(ItemTypeKeys.GOLDEN_AXE),
            TagEntry.valueEntry(ItemTypeKeys.GOLDEN_SHOVEL),
            TagEntry.valueEntry(ItemTypeKeys.GOLDEN_HOE),
        )
    ),

    DIAMOND_TOOLS_KEY(
        TagKey.create(RegistryKey.ITEM, Key.key("surf", "diamond_tools")),
        objectSetOf(
            TagEntry.valueEntry(ItemTypeKeys.DIAMOND_PICKAXE),
            TagEntry.valueEntry(ItemTypeKeys.DIAMOND_AXE),
            TagEntry.valueEntry(ItemTypeKeys.DIAMOND_SHOVEL),
            TagEntry.valueEntry(ItemTypeKeys.DIAMOND_HOE),
        )
    ),

    NETHERITE_TOOLS_KEY(
        TagKey.create(RegistryKey.ITEM, Key.key("surf", "netherite_tools")),
        objectSetOf(
            TagEntry.valueEntry(ItemTypeKeys.NETHERITE_PICKAXE),
            TagEntry.valueEntry(ItemTypeKeys.NETHERITE_AXE),
            TagEntry.valueEntry(ItemTypeKeys.NETHERITE_SHOVEL),
            TagEntry.valueEntry(ItemTypeKeys.NETHERITE_HOE),
        )
    ),

    TOOLS_KEY(
        TagKey.create(RegistryKey.ITEM, Key.key("surf", "tools")),
        objectSetOf(
            DIAMOND_TOOLS_KEY.tagEntries
        )
    )

}