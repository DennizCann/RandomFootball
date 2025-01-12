package com.denizcan.randomfootball.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.denizcan.randomfootball.data.model.Team
import kotlinx.coroutines.flow.Flow

@Dao
interface TeamDao {
    @Insert
    suspend fun insertTeam(team: Team): Long

    @Delete
    suspend fun deleteTeam(team: Team)

    @Query("SELECT * FROM teams WHERE leagueId = :leagueId")
    fun getTeamsByLeagueId(leagueId: Long): Flow<List<Team>>

    @Query("DELETE FROM teams WHERE leagueId = :leagueId")
    suspend fun deleteTeamsByLeagueId(leagueId: Long)

    @Query("SELECT * FROM teams WHERE teamId = :teamId")
    fun getTeamById(teamId: Long): Flow<Team?>

    @Query("""
        SELECT t.* FROM teams t
        INNER JOIN leagues l ON t.leagueId = l.leagueId
        WHERE l.gameId = :gameId
    """)
    suspend fun getTeamsByGameId(gameId: Long): List<Team>
} 