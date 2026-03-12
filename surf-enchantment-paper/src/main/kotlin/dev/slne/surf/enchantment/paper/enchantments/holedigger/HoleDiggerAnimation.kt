package dev.slne.surf.enchantment.paper.enchantments.holedigger

import com.github.shynixn.mccoroutine.folia.launch
import com.github.shynixn.mccoroutine.folia.ticks
import dev.slne.surf.enchantment.paper.plugin
import dev.slne.surf.surfapi.core.api.messages.adventure.playSound
import kotlinx.coroutines.delay
import net.kyori.adventure.sound.Sound
import org.bukkit.Particle
import org.bukkit.block.Block
import kotlin.math.cos
import kotlin.math.sin
import org.bukkit.Sound as BukkitSound

object HoleDiggerAnimation {
    fun playSpiralEffect(centerBlock: Block, props: PickaxeProperties) {
        val center = centerBlock.location.toCenterLocation()

        val height = props.y.toDouble()
        val radiusX = props.x / 2.0
        val radiusZ = props.z / 2.0

        val points = 120
        val iterations = 4
        val pointsPerTick = 3

        val nearbyPlayers = center.getNearbyPlayers(32.0)

        nearbyPlayers.forEach { player ->
            player.playSound(true) {
                type(BukkitSound.ENTITY_ZOMBIE_VILLAGER_CURE)
                source(Sound.Source.NEUTRAL)
                pitch(0.5f)
            }
        }

        plugin.launch {
            for (step in 0 until points step pointsPerTick) {
                for (subStep in 0 until pointsPerTick) {
                    val currentStep = step + subStep
                    if (currentStep >= points) break

                    val progress = currentStep.toDouble() / points
                    val angle = 2 * Math.PI * iterations * progress

                    val x = cos(angle) * radiusX * progress
                    val z = sin(angle) * radiusZ * progress
                    val y = (progress - 0.5) * height

                    val particleLoc = center.clone().add(x, y, z)

                    nearbyPlayers.forEach { player ->
                        player.spawnParticle(
                            Particle.ENCHANT,
                            particleLoc,
                            5,
                            0.05, 0.05, 0.05, 0.01
                        )

                        player.spawnParticle(
                            Particle.TRIAL_SPAWNER_DETECTION,
                            particleLoc,
                            1,
                            0.0, 0.0, 0.0, 0.0
                        )
                    }
                }

                delay(1.ticks)
            }
        }
    }
}