package com.denizcan.randomfootball.ui.screens.playmatch

import com.denizcan.randomfootball.data.model.Player
import com.denizcan.randomfootball.data.model.Team
import kotlin.random.Random

object PlayMatchUtils {
    // 1. Oyuncu Seçim Metodları
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
        val eligiblePlayers = players.filter { 
            it.playerId != scorer.playerId && it.position != "Goalkeeper" 
        }
        if (eligiblePlayers.isEmpty()) return null

        return when (Random.nextInt(100)) {
            in 0..50 -> eligiblePlayers.filter { it.position == "Midfielder" }  // %50 Orta Saha
            in 51..80 -> eligiblePlayers.filter { it.position == "Forward" }    // %30 Forvet
            else -> eligiblePlayers.filter { it.position == "Defender" }        // %20 Defans
        }.randomOrNull()
    }

    // 2. Maç Olayları ve Veri Modelleri
    enum class MatchPhase {
        NOT_STARTED,      // 0. dakika - Maç başlamadı
        FIRST_PHASE,      // 15. dakika
        SECOND_PHASE,     // 30. dakika
        THIRD_PHASE,      // 45. dakika
        FOURTH_PHASE,     // 60. dakika
        FIFTH_PHASE,      // 75. dakika
        SIXTH_PHASE,      // 90. dakika
        MATCH_ENDED       // 95. dakika - Maç bitti
    }

    data class MatchEvent(
        val minute: Int,
        val eventType: EventType,
        val player: Player,
        val assist: Player? = null,
        val team: Team
    )

    enum class EventType {
        GOAL
    }

    // 3. Maç Simülasyon Yardımcı Metodları
    fun shouldScore(): Boolean {
        return (1..5).random() == 5  // %20 gol şansı
    }

    fun getMinuteForPhase(phase: MatchPhase): Int {
        return when (phase) {
            MatchPhase.NOT_STARTED -> 0
            MatchPhase.FIRST_PHASE -> 15
            MatchPhase.SECOND_PHASE -> 30
            MatchPhase.THIRD_PHASE -> 45
            MatchPhase.FOURTH_PHASE -> 60
            MatchPhase.FIFTH_PHASE -> 75
            MatchPhase.SIXTH_PHASE -> 90
            MatchPhase.MATCH_ENDED -> 95
        }
    }

    fun getNextPhase(currentPhase: MatchPhase): MatchPhase {
        return when (currentPhase) {
            MatchPhase.NOT_STARTED -> MatchPhase.FIRST_PHASE
            MatchPhase.FIRST_PHASE -> MatchPhase.SECOND_PHASE
            MatchPhase.SECOND_PHASE -> MatchPhase.THIRD_PHASE
            MatchPhase.THIRD_PHASE -> MatchPhase.FOURTH_PHASE
            MatchPhase.FOURTH_PHASE -> MatchPhase.FIFTH_PHASE
            MatchPhase.FIFTH_PHASE -> MatchPhase.SIXTH_PHASE
            MatchPhase.SIXTH_PHASE -> MatchPhase.MATCH_ENDED
            MatchPhase.MATCH_ENDED -> MatchPhase.MATCH_ENDED
        }
    }
}