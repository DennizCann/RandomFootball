package com.denizcan.randomfootball.util

import com.denizcan.randomfootball.data.model.Fixture
import com.denizcan.randomfootball.data.model.Team

object FixtureGenerator {
    fun generateFixtures(teams: List<Team>, leagueId: Long, gameId: Long): List<Fixture> {
        val fixtures = mutableListOf<Fixture>()
        val teamCount = teams.size
        val teamsList = teams.toMutableList()
        
        // İlk yarı fikstürü
        for (week in 0 until teamCount-1) {
            for (i in 0 until teamCount/2) {
                val home = teamsList[i]
                val away = teamsList[teamCount-1-i]
                
                fixtures.add(
                    Fixture(
                        gameId = gameId,
                        leagueId = leagueId,
                        homeTeamId = home.teamId,
                        awayTeamId = away.teamId,
                        week = week + 1
                    )
                )
            }
            // Takımları döndür (ilk takım sabit)
            teamsList.subList(1, teamsList.size).let { subList ->
                val last = subList.last()
                subList.clear()
                subList.add(0, last)
                subList.addAll(teams.subList(1, teams.size - 1))
            }
        }
        
        // İkinci yarı fikstürü
        val firstHalf = fixtures.toList()
        firstHalf.forEach { fixture ->
            fixtures.add(
                fixture.copy(
                    homeTeamId = fixture.awayTeamId,
                    awayTeamId = fixture.homeTeamId,
                    week = fixture.week + teamCount - 1
                )
            )
        }
        
        return fixtures
    }
} 