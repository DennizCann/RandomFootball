package com.denizcan.randomfootball.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.denizcan.randomfootball.data.model.League
import kotlinx.coroutines.flow.Flow

@Dao
interface LeagueDao {
    @Insert
    suspend fun insertLeague(league: League): Long

    @Delete
    suspend fun deleteLeague(league: League)

    @Query("SELECT * FROM leagues WHERE gameId = :gameId")
    fun getLeaguesByGameId(gameId: Long): Flow<List<League>>

    @Query("DELETE FROM leagues WHERE gameId = :gameId")
    suspend fun deleteLeaguesByGameId(gameId: Long)

    @Query("SELECT * FROM leagues")
    fun getAllLeagues(): Flow<List<League>>

    @Query("SELECT * FROM leagues WHERE leagueId = :leagueId")
    fun getLeagueById(leagueId: Long): Flow<League?>
} 