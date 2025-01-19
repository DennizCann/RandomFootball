package com.denizcan.randomfootball.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.denizcan.randomfootball.data.model.Game
import kotlinx.coroutines.flow.Flow

@Dao
interface GameDao {
    @Insert
    suspend fun insertGame(game: Game): Long

    @Query("SELECT * FROM games ORDER BY creationDate DESC")
    fun getAllGames(): Flow<List<Game>>

    @Delete
    suspend fun deleteGame(game: Game)

    @Query("UPDATE games SET selectedTeamId = :teamId WHERE gameId = :gameId")
    suspend fun updateSelectedTeam(gameId: Long, teamId: Long)

    @Query("SELECT selectedTeamId FROM games WHERE gameId = :gameId")
    suspend fun getSelectedTeamId(gameId: Long): Long?
}