package com.denizcan.randomfootball.util

import com.denizcan.randomfootball.data.model.Fixture
import com.denizcan.randomfootball.data.model.Team

object FixtureGenerator {
    fun generateFixtures(teams: List<Team>, leagueId: Long, gameId: Long): List<Fixture> {
        val fixtures = mutableListOf<Fixture>()
        val teamCount = teams.size
        val rounds = teamCount - 1
        val matchesPerRound = teamCount / 2
        
        // Takımların listesini oluştur (ilk takım sabit kalacak)
        val teamsList = teams.map { it.teamId }.toMutableList()
        
        // İlk yarı fikstürü
        for (round in 0 until rounds) {
            val roundFixtures = mutableListOf<Fixture>()
            
            // Her haftadaki maçları oluştur
            for (match in 0 until matchesPerRound) {
                val team1 = teamsList[match]
                val team2 = teamsList[teamCount - 1 - match]
                
                // Tek sayılı haftalarda team1 ev sahibi, çift sayılı haftalarda team2 ev sahibi
                val (homeTeam, awayTeam) = if (round % 2 == 0) {
                    team1 to team2
                } else {
                    team2 to team1
                }
                
                roundFixtures.add(
                    Fixture(
                        gameId = gameId,
                        leagueId = leagueId,
                        homeTeamId = homeTeam,
                        awayTeamId = awayTeam,
                        week = round + 1
                    )
                )
            }
            
            fixtures.addAll(roundFixtures)
            
            // Takım listesini döndür (ilk takım sabit)
            val lastTeam = teamsList.removeAt(teamsList.lastIndex)
            teamsList.add(1, lastTeam)
        }
        
        // İkinci yarı fikstürü (ev/deplasman takımları yer değiştirir)
        val firstHalf = fixtures.toList()
        firstHalf.forEach { fixture ->
            fixtures.add(
                fixture.copy(
                    homeTeamId = fixture.awayTeamId,
                    awayTeamId = fixture.homeTeamId,
                    week = fixture.week + rounds
                )
            )
        }
        
        return fixtures
    }
} 