package com.denizcan.randomfootball.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.denizcan.randomfootball.data.model.Player
import kotlinx.coroutines.flow.Flow

@Dao
interface PlayerDao {
    @Insert
    suspend fun insertPlayer(player: Player): Long

    @Delete
    suspend fun deletePlayer(player: Player)

    @Query("SELECT * FROM players WHERE teamId = :teamId")
    fun getPlayersByTeamId(teamId: Long): Flow<List<Player>>

    @Query("DELETE FROM players WHERE playerId = :playerId")
    suspend fun deletePlayerById(playerId: Long)

    @Query("DELETE FROM players WHERE teamId = :teamId")
    suspend fun deletePlayersByTeamId(teamId: Long)
}
