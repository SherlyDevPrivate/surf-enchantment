package dev.slne.surf.enchantment.paper.enchantments.rocketride

import com.github.shynixn.mccoroutine.folia.launch
import com.github.shynixn.mccoroutine.folia.ticks
import dev.slne.surf.enchantment.paper.plugin
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import org.bukkit.GameMode
import org.bukkit.entity.HappyGhast
import org.bukkit.entity.Player
import org.bukkit.util.Vector
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

object RocketRideBoostService {

    private val activeBoostStates = ConcurrentHashMap<UUID, BoostState>()
    private var tickerJob: Job? = null

    private data class BoostState(
        val ghast: HappyGhast,
        val rider: Player,
        val power: Double,
        val upward: Double,
        val totalTicks: Int,
        val startVelocity: Vector,
        val lookDirection: Vector,
        var currentTick: Int = 0
    )

    fun isBoosting(ghast: HappyGhast): Boolean = activeBoostStates.containsKey(ghast.uniqueId)

    fun startBoost(ghast: HappyGhast, rider: Player, power: Double, upward: Double, durationTicks: Int) {
        if (rider.gameMode == GameMode.SPECTATOR) return

        val state = BoostState(
            ghast = ghast,
            rider = rider,
            power = power,
            upward = upward,
            totalTicks = durationTicks,
            startVelocity = ghast.velocity.clone(),
            lookDirection = rider.location.direction.normalize()
        )

        activeBoostStates[ghast.uniqueId] = state
        ensureTickerStarted()
    }

    private fun ensureTickerStarted() {
        if (tickerJob != null && tickerJob?.isActive == true) return

        tickerJob = plugin.launch {
            while (activeBoostStates.isNotEmpty()) {
                val iterator = activeBoostStates.values.iterator()

                while (iterator.hasNext()) {
                    val state = iterator.next()

                    if (!state.ghast.isValid || state.ghast.isDead) {
                        iterator.remove()
                        continue
                    }

                    if (state.currentTick >= state.totalTicks) {
                        state.ghast.velocity = state.startVelocity
                        iterator.remove()
                        continue
                    }

                    updateGhast(state)
                    state.currentTick++
                }
                delay(1.ticks)
            }
        }
    }

    private fun updateGhast(state: BoostState) {
        val boostTicks = (state.totalTicks * 0.75).toInt()
        val slowTicks = state.totalTicks - boostTicks

        val newVelocity: Vector

        if (state.currentTick < boostTicks) {

            val progress = state.currentTick.toDouble() / boostTicks
            val multiplier = sin(progress * (PI / 2))

            newVelocity = state.startVelocity.clone().add(
                Vector(
                    state.lookDirection.x * state.power * multiplier,
                    state.lookDirection.y * state.power * multiplier + state.upward * multiplier,
                    state.lookDirection.z * state.power * multiplier
                )
            )
        } else {
            val slowProgress = (state.currentTick - boostTicks).toDouble() / slowTicks
            val multiplier = cos(slowProgress * (PI / 2))

            val peakMultiplier = 1.0
            val boostVector = Vector(
                state.lookDirection.x * state.power * peakMultiplier,
                state.lookDirection.y * state.power * peakMultiplier + state.upward * peakMultiplier,
                state.lookDirection.z * state.power * peakMultiplier
            )
            val peakVelocity = state.startVelocity.clone().add(boostVector)

            newVelocity = peakVelocity.multiply(multiplier).add(state.startVelocity.clone().multiply(1 - multiplier))
        }

        state.ghast.velocity = newVelocity
        state.rider.fallDistance = 0f
    }

    fun stopBoost(ghast: HappyGhast) {
        activeBoostStates.remove(ghast.uniqueId)
    }
}