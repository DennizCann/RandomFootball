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
                val home = teamsList[match]
                val away = teamsList[teamCount - 1 - match]
                
                // Çift sayılı haftalarda ev sahibi/deplasman takımlarını değiştir
                // Bu sayede takımlar daha dengeli bir şekilde ev/deplasman maçı yapacak
                val fixture = if ((round + match) % 2 == 0) {
                    Fixture(
                        gameId = gameId,
                        leagueId = leagueId,
                        homeTeamId = home,
                        awayTeamId = away,
                        week = round + 1
                    )
                } else {
                    Fixture(
                        gameId = gameId,
                        leagueId = leagueId,
                        homeTeamId = away,
                        awayTeamId = home,
                        week = round + 1
                    )
                }
                
                roundFixtures.add(fixture)
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