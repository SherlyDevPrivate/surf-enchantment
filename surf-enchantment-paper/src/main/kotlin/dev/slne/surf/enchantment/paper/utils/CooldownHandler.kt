package dev.slne.surf.enchantment.paper.utils

import com.github.benmanes.caffeine.cache.Caffeine
import com.sksamuel.aedile.core.expireAfterWrite
import dev.slne.surf.enchantment.api.enchantment.EnchantmentJob
import dev.slne.surf.surfapi.bukkit.api.extensions.server
import dev.slne.surf.surfapi.core.api.messages.adventure.sendText
import dev.slne.surf.surfapi.core.api.messages.builder.SurfComponentBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.coroutineScope
import org.bukkit.entity.Player
import java.time.OffsetDateTime
import java.util.*
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds

class CooldownHandler(
    private val expirationMessage: SurfComponentBuilder.() -> Unit = {},
    private val notReadyMessage: SurfComponentBuilder.(Long) -> Unit = {},
    cooldownExpiration: Duration = 5.minutes,
    lastMessageExpiration: Duration = 3.seconds
) : EnchantmentJob() {
    private val expirationListeners = mutableListOf<suspend CoroutineScope.(UUID) -> Unit>()

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

                coroutineScope {
                    expirationListeners.forEach { listener ->
                        listener.invoke(this, uuid)
                    }
                }
            }
        }
    }

    fun checkCooldown(uuid: UUID, receiverPlayer: Player? = null): Boolean {
        val expireTime = cooldowns.getIfPresent(uuid) ?: return true

        if (lastMessage.getIfPresent(uuid) == null) {
            val secondsLeft = expireTime.toEpochSecond() - OffsetDateTime.now().toEpochSecond()

            val player = receiverPlayer ?: server.getPlayer(uuid)

            player?.sendText {
                notReadyMessage(this, secondsLeft)
            }

            lastMessage.put(uuid, Unit)
        }

        return false
    }

    fun applyCooldown(uuid: UUID, cooldown: Duration) {
        cooldowns.put(uuid, OffsetDateTime.now().plusSeconds(cooldown.inWholeSeconds))
    }

    fun invalidateCooldown(uuid: UUID) {
        cooldowns.invalidate(uuid)
    }

    fun registerExpirationListener(listener: suspend CoroutineScope.(UUID) -> Unit) {
        expirationListeners.add(listener)
    }
}