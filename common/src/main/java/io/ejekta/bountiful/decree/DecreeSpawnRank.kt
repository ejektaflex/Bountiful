package io.ejekta.bountiful.decree

import io.ejekta.bountiful.bounty.DecreeData
import kotlin.math.min
import kotlin.random.Random

enum class DecreeSpawnRank(val populateFunc: DecreeData.(optionIds: List<String>) -> Unit) {
    // Random number of decrees, randomly picked
    RANDOM({ optionIds ->
        val shuffled = optionIds.shuffled().toMutableList()
        // Number of total revealables, or reveal rank, whichever comes first
        for (i in 0 until min(shuffled.size, rank)) {
            // 100% chance of 1, 33% chance each of more stacking until failure
            if (i == 0 || Random.nextDouble() < 0.33) {
                ids.add(shuffled.removeLast())
            } else {
                 break
            }
        }
    }),
    // Constant number of decrees, randomly picked
    CONSTANT({ optionIds ->
        val shuffled = optionIds.shuffled()
        for (i in 0 until min(optionIds.size, rank)) {
            ids.add(shuffled[i])
        }
    })
}