package com.denizcan.randomfootball.util

import com.denizcan.randomfootball.data.model.Player
import android.util.Log

object TeamUtils {
    fun getBestEleven(players: List<Player>, formation: String): List<Player> {
        val formationRows = formation.split("-").map { it.toInt() }

        return buildList {
            // En iyi kaleci
            players
                .filter { it.position == "Goalkeeper" }
                .sortedWith(
                    compareByDescending<Player> { it.skill }
                        .thenBy { it.shirtNumber }
                )
                .firstOrNull()?.let { add(it) }

            // En iyi defanslar
            val defenders = players
                .filter { it.position == "Defender" }
                .sortedWith(
                    compareByDescending<Player> { it.skill }
                        .thenBy { it.shirtNumber }
                )
                .take(formationRows[0].coerceAtMost(
                    players.count { it.position == "Defender" }
                ))
            addAll(defenders)

            // En iyi orta sahalar
            val midfielders = players
                .filter { it.position == "Midfielder" }
                .sortedWith(
                    compareByDescending<Player> { it.skill }
                        .thenBy { it.shirtNumber }
                )
                .take(formationRows[1].coerceAtMost(
                    players.count { it.position == "Midfielder" }
                ))
            addAll(midfielders)

            // En iyi forvetler
            val forwards = players
                .filter { it.position == "Forward" }
                .sortedWith(
                    compareByDescending<Player> { it.skill }
                        .thenBy { it.shirtNumber }
                )
                .take(formationRows[2].coerceAtMost(
                    players.count { it.position == "Forward" }
                ))
            addAll(forwards)
        }
    }

    // selectFirstEleven fonksiyonu getBestEleven ile aynı işi yapıyor,
    // bu yüzden getBestEleven'ı kullanabiliriz
    fun selectFirstEleven(players: List<Player>, formation: String): List<Player> {
        return getBestEleven(players, formation)
    }

    fun calculateAttackPoints(players: List<Player>): Int {
        return players
            .filter { it.position == "Midfielder" || it.position == "Forward" }
            .sumOf { it.skill }
    }

    fun calculateDefensePoints(players: List<Player>): Int {
        return players
            .filter { it.position == "Goalkeeper" || it.position == "Defender" }
            .sumOf { it.skill }
    }

    fun calculateTeamStats(players: List<Player>, formation: String): TeamStats {
        // İlk 11'i seç
        val firstEleven = selectFirstEleven(players, formation)

        // Savunma puanı (Kaleci + Defans oyuncuları)
        val defensePoints = firstEleven
            .filter { it.position == "Goalkeeper" || it.position == "Defender" }
            .sumOf { it.skill }

        // Hücum puanı (Orta saha + Forvet oyuncuları)
        val attackPoints = firstEleven
            .filter { it.position == "Midfielder" || it.position == "Forward" }
            .sumOf { it.skill }

        Log.d("TeamUtils", """
            Team Stats:
            First Eleven Size: ${firstEleven.size}
            Defense Players: ${firstEleven.count { it.position == "Goalkeeper" || it.position == "Defender" }}
            Attack Players: ${firstEleven.count { it.position == "Midfielder" || it.position == "Forward" }}
            Defense Points: $defensePoints
            Attack Points: $attackPoints
        """.trimIndent())

        return TeamStats(attackPoints, defensePoints)
    }
    data class TeamStats(
        val attackPoints: Int,
        val defensePoints: Int
    )
}

