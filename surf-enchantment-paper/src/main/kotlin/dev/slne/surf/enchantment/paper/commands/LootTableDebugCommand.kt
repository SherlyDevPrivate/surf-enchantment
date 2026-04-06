package dev.slne.surf.enchantment.paper.commands

import dev.jorel.commandapi.kotlindsl.commandAPICommand
import dev.jorel.commandapi.kotlindsl.playerExecutor
import dev.slne.surf.enchantment.paper.plugin
import dev.slne.surf.enchantment.paper.utils.SurfEnchantmentPermissionRegistry
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.Registry
import org.bukkit.inventory.ItemStack
import org.bukkit.loot.LootContext
import java.io.File
import java.util.*

fun surfEnchantmentDebugLootTableCommand() = commandAPICommand("loottabledebug") {
    withPermission(SurfEnchantmentPermissionRegistry.COMMAND_BASE)
    playerExecutor { player, _ ->
        player.sendMessage(Component.text("--- Starte Deep Scan & Enchantment Analyse ---", NamedTextColor.GOLD))

        val lootFile = File(plugin.dataFolder, "loot_full_export.txt")
        val enchFile = File(plugin.dataFolder, "enchantment_locations.txt")
        lootFile.parentFile.mkdirs()

        val lootWriter = lootFile.bufferedWriter()
        val enchWriter = enchFile.bufferedWriter()

        val enchantmentMap = mutableMapOf<String, MutableMap<String, MutableSet<String>>>()
        val allKeys = Registry.LOOT_TABLES.keyStream()

        allKeys.forEach { namespacedKey ->
            val lootTable = Bukkit.getLootTable(namespacedKey) ?: return@forEach
            val keyString = namespacedKey.toString()
            val uniqueItems = mutableSetOf<String>()

            val context = LootContext.Builder(player.location)
                .lootedEntity(player)
                .killer(player)
                .build()

            try {
                repeat(500) {
                    val items = lootTable.populateLoot(Random(), context)
                    items.forEach { stack ->
                        uniqueItems.add(formatItemStack(stack))
                        analyzeEnchantments(stack, keyString, enchantmentMap)
                    }
                }
            } catch (e: Exception) {
                player.sendMessage(
                    Component.text(
                        "Fehler beim Verarbeiten der Loot-Tabelle $keyString: ${e.message}",
                        NamedTextColor.RED
                    )
                )
            }

            if (uniqueItems.isNotEmpty()) {
                lootWriter.write("$keyString\n")
                uniqueItems.sorted().forEach { lootWriter.write("- $it\n") }
                lootWriter.write("\n")
            }
        }

        enchantmentMap.toSortedMap().forEach { (enchName, locations) ->
            enchWriter.write("Enchantment: $enchName\n")
            locations.toSortedMap().forEach { (table, types) ->
                val typeString = types.joinToString(", ")
                enchWriter.write("- $table ($typeString)\n")
            }
            enchWriter.write("\n")
        }

        lootWriter.flush()
        enchWriter.flush()
        lootWriter.close()
        enchWriter.close()

        player.sendMessage(
            Component.text(
                "Export fertig! Zwei Dateien wurden im Plugin-Ordner erstellt.",
                NamedTextColor.GREEN
            )
        )
    }
}

fun analyzeEnchantments(
    stack: ItemStack,
    tableKey: String,
    map: MutableMap<String, MutableMap<String, MutableSet<String>>>
) {
    val meta = stack.itemMeta ?: return
    val isBook = stack.type == org.bukkit.Material.ENCHANTED_BOOK
    val typeLabel = if (isBook) "Book" else "Items"

    val addEnch = { enchKey: String ->
        val friendlyName = enchKey.replace("_", " ").split(':').last().replaceFirstChar { it.uppercase() }
        val locations = map.getOrPut(friendlyName) { mutableMapOf() }
        val types = locations.getOrPut(tableKey) { mutableSetOf() }
        types.add(typeLabel)
    }

    if (meta is org.bukkit.inventory.meta.EnchantmentStorageMeta) {
        meta.storedEnchants.keys.forEach { addEnch(it.key.key) }
    }

    if (meta.hasEnchants()) {
        meta.enchants.keys.forEach { addEnch(it.key.key) }
    }
}

fun formatItemStack(stack: ItemStack): String {
    val name = stack.type.toString().lowercase().replace("_", " ").split(' ')
        .joinToString(" ") { it.replaceFirstChar { char -> char.uppercase() } }

    val enchantments = mutableListOf<String>()

    if (stack.hasItemMeta()) {
        val meta = stack.itemMeta

        if (meta is org.bukkit.inventory.meta.EnchantmentStorageMeta) {
            meta.storedEnchants.forEach { (ench, level) ->
                enchantments.add("${ench.key.key.replace("_", " ")} $level")
            }
        }

        if (meta.hasEnchants()) {
            meta.enchants.forEach { (ench, level) ->
                enchantments.add("${ench.key.key.replace("_", " ")} $level")
            }
        }
    }

    return if (enchantments.isEmpty()) {
        name
    } else {
        "$name (${enchantments.joinToString(", ").lowercase()})"
    }
}