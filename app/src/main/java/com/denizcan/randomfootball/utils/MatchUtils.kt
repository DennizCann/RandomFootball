package com.denizcan.randomfootball.utils

import com.denizcan.randomfootball.data.model.Player
import kotlin.random.Random

object MatchUtils {
    fun selectScorer(players: List<Player>): Player {
        return when (Random.nextInt(100)) {
            in 0..55 -> players.filter { it.position == "Forward" }  // %55 Forvet
            in 56..85 -> players.filter { it.position == "Midfielder" }  // %30 Orta Saha
            else -> players.filter { it.position == "Defender" }  // %15 Defans
        }.randomOrNull() ?: players.filter { 
            it.position != "Goalkeeper" 
        }.random()
    }

    fun selectAssist(players: List<Player>, scorer: Player): Player? {
        val eligiblePlayers = players.filter { it.playerId != scorer.playerId && it.position != "Goalkeeper" }
        if (eligiblePlayers.isEmpty()) return null

        return when (Random.nextInt(100)) {
            in 0..50 -> eligiblePlayers.filter { it.position == "Midfielder" }  // %50 Orta Saha
            in 51..80 -> eligiblePlayers.filter { it.position == "Forward" }    // %30 Forvet
            else -> eligiblePlayers.filter { it.position == "Defender" }        // %20 Defans
        }.randomOrNull()
    }
} 