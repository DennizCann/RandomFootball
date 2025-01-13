package com.denizcan.randomfootball.data.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.denizcan.randomfootball.data.model.LeagueTable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

@Dao
interface LeagueTableDao {
    @Insert
    suspend fun insertLeagueTable(leagueTable: LeagueTable): Long

    @Insert
    suspend fun insertLeagueTables(leagueTables: List<LeagueTable>)

    @Update
    suspend fun updateLeagueTable(leagueTable: LeagueTable)

    @Query("""
        SELECT * FROM league_tables 
        WHERE leagueId = :leagueId 
        ORDER BY points DESC, goalDifference DESC, goalsFor DESC
    """)
    fun getLeagueTableByLeagueId(leagueId: Long): Flow<List<LeagueTable>>

    @Query("SELECT * FROM league_tables WHERE teamId = :teamId")
    fun getLeagueTableByTeamId(teamId: Long): Flow<LeagueTable?>

    // Maç sonucu girildiğinde puan tablosunu güncelle
    @Transaction
    suspend fun updateAfterMatch(
        leagueId: Long,
        homeTeamId: Long,
        awayTeamId: Long,
        homeScore: Int,
        awayScore: Int
    ) {
        // Ev sahibi takımın verilerini güncelle
        val homeTeam = getLeagueTableByTeamId(homeTeamId).first()
        homeTeam?.let {
            val newGoalsFor = it.goalsFor + homeScore
            val newGoalsAgainst = it.goalsAgainst + awayScore
            updateLeagueTable(it.copy(
                played = it.played + 1,
                won = it.won + if (homeScore > awayScore) 1 else 0,
                drawn = it.drawn + if (homeScore == awayScore) 1 else 0,
                lost = it.lost + if (homeScore < awayScore) 1 else 0,
                points = it.points + when {
                    homeScore > awayScore -> 3
                    homeScore == awayScore -> 1
                    else -> 0
                },
                goalsFor = newGoalsFor,
                goalsAgainst = newGoalsAgainst,
                goalDifference = newGoalsFor - newGoalsAgainst
            ))
        }

        // Deplasman takımının verilerini güncelle
        val awayTeam = getLeagueTableByTeamId(awayTeamId).first()
        awayTeam?.let {
            val newGoalsFor = it.goalsFor + awayScore
            val newGoalsAgainst = it.goalsAgainst + homeScore
            updateLeagueTable(it.copy(
                played = it.played + 1,
                won = it.won + if (awayScore > homeScore) 1 else 0,
                drawn = it.drawn + if (homeScore == awayScore) 1 else 0,
                lost = it.lost + if (awayScore < homeScore) 1 else 0,
                points = it.points + when {
                    awayScore > homeScore -> 3
                    homeScore == awayScore -> 1
                    else -> 0
                },
                goalsFor = newGoalsFor,
                goalsAgainst = newGoalsAgainst,
                goalDifference = newGoalsFor - newGoalsAgainst
            ))
        }

        // Hemen sıralamaları güncelle
        updatePositions(leagueId)
    }

    @Query("""
        UPDATE league_tables
        SET position = (
            SELECT COUNT(*) + 1
            FROM league_tables AS t2
            JOIN teams ON teams.teamId = t2.teamId
            WHERE t2.leagueId = league_tables.leagueId
            AND (
                t2.points > league_tables.points 
                OR (t2.points = league_tables.points 
                    AND t2.goalDifference > league_tables.goalDifference)
                OR (t2.points = league_tables.points 
                    AND t2.goalDifference = league_tables.goalDifference 
                    AND t2.goalsFor > league_tables.goalsFor)
                OR (t2.points = league_tables.points 
                    AND t2.goalDifference = league_tables.goalDifference 
                    AND t2.goalsFor = league_tables.goalsFor 
                    AND (
                        SELECT name FROM teams WHERE teamId = t2.teamId
                    ) < (
                        SELECT name FROM teams WHERE teamId = league_tables.teamId
                    )
                )
            )
        )
        WHERE leagueId = :leagueId
    """)
    suspend fun updatePositions(leagueId: Long)
} 