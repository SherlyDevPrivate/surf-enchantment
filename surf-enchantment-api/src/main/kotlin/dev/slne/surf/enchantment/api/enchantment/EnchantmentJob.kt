package dev.slne.surf.enchantment.api.enchantment

import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

abstract class EnchantmentJob(
    val delay: Duration = 1.seconds,
) {
    private lateinit var job: Job

    abstract suspend fun tick()

    fun start() {
        job = EnchantmentManager.launch {
            while (isActive) {
                tick()
                delay(delay)
            }
        }
    }

    fun stop() {
        if (::job.isInitialized && job.isActive) {
            job.cancel()
        }
    }
}