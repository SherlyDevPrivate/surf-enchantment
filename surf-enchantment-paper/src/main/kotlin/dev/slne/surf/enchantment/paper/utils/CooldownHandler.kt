package dev.slne.surf.enchantment.paper.utils

import com.github.benmanes.caffeine.cache.Caffeine
import com.sksamuel.aedile.core.expireAfterWrite
import dev.slne.surf.enchantment.api.enchantment.EnchantmentJob
import dev.slne.surf.surfapi.bukkit.api.extensions.server
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import dev.slne.surf.surfapi.core.api.messages.builder.SurfComponentBuilder
import org.bukkit.entity.Player
import java.time.OffsetDateTime
import java.util.*
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class CooldownHandler(
    private val expirationMessage: SurfComponentBuilder.() -> Unit,
    private val notReadyMessage: SurfComponentBuilder.(Long) -> Unit,
    cooldownExpiration: Duration = 5.minutes,
    lastMessageExpiration: Duration = 3.seconds
) : EnchantmentJob() {
    private val lastMessage = Caffeine.newBuilder()
        .expireAfterWrite(lastMessageExpiration)
        .maximumSize(10_000)
        .build<UUID, Unit>()

    private val cooldowns = Caffeine.newBuilder()
        .expireAfterWrite(cooldownExpiration)
        .maximumSize(10_000)
        .build<UUID, OffsetDateTime>()

    override suspend fun tick() {
        val now = OffsetDateTime.now()
        cooldowns.asMap().forEach { (uuid, expireTime) ->
            if (expireTime.isBefore(now)) {
                cooldowns.invalidate(uuid)

                server.getPlayer(uuid)?.sendText(expirationMessage)
            }
        }
    }

    fun checkCooldown(player: Player): Boolean {
        val expireTime = cooldowns.getIfPresent(player.uniqueId) ?: return true

        if (lastMessage.getIfPresent(player.uniqueId) == null) {
            val secondsLeft = expireTime.toEpochSecond() - OffsetDateTime.now().toEpochSecond()

            player.sendText {
                notReadyMessage(this, secondsLeft)
            }

            lastMessage.put(player.uniqueId, Unit)
        }

        return false
    }

    fun applyCooldown(player: Player, cooldown: Duration) {
        cooldowns.put(player.uniqueId, OffsetDateTime.now().plusSeconds(cooldown.inWholeSeconds))
    }
}