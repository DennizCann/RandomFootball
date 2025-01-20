package com.denizcan.randomfootball.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.denizcan.randomfootball.data.model.Manager
import kotlinx.coroutines.flow.Flow

@Dao
interface ManagerDao {

    @Query("SELECT * FROM managers WHERE managerId = :managerId")
    fun getManagerById(managerId: Long): Flow<Manager?>

    @Insert
    suspend fun insertManager(manager: Manager): Long

    @Query("SELECT * FROM managers WHERE teamId = :teamId")
    fun getManagerByTeamId(teamId: Long): Flow<Manager?>

    @Query("SELECT * FROM managers")
    fun getAllManagers(): Flow<List<Manager>>

    @Delete
    suspend fun deleteManager(manager: Manager)

    @Update
    suspend fun updateManager(manager: Manager)

    @Query("SELECT * FROM managers WHERE teamId = :teamId LIMIT 1")
    suspend fun getManagerByTeamIdSync(teamId: Long): Manager?

    @Query("UPDATE managers SET formation = :formation WHERE managerId = :managerId")
    suspend fun updateManagerFormation(managerId: Long, formation: String)

    @Query("SELECT * FROM managers WHERE gameId = :gameId")
    fun getManagersByGameId(gameId: Long): Flow<List<Manager>>

    @Query("UPDATE managers SET formation = :formation WHERE managerId = :managerId")
    suspend fun updateFormation(managerId: Long, formation: String)

}