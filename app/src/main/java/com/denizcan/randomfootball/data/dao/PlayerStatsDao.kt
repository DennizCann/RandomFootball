package com.denizcan.randomfootball.data.dao

import android.util.Log
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import androidx.room.Transaction
import androidx.room.Embedded
import androidx.room.Relation
import com.denizcan.randomfootball.data.model.Player
import com.denizcan.randomfootball.data.model.PlayerStats
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

@Dao
interface PlayerStatsDao {
    @Insert
    suspend fun insertPlayerStats(playerStats: PlayerStats): Long

    @Update
    suspend fun updatePlayerStats(playerStats: PlayerStats)

    @Query("SELECT * FROM player_stats WHERE playerId = :playerId AND gameId = :gameId")
    fun getPlayerStats(playerId: Long, gameId: Long): Flow<PlayerStats?>

    @Query("SELECT * FROM player_stats WHERE gameId = :gameId")
    fun getAllPlayerStatsByGame(gameId: Long): Flow<List<PlayerStats>>

    // Gol krallığı sıralaması
    @Transaction
    @Query("""
        SELECT p.*, COALESCE(ps.goals, 0) as goals,
               COALESCE(ps.assists, 0) as assists,
               COALESCE(ps.appearances, 0) as appearances
        FROM players p 
        LEFT JOIN player_stats ps ON p.playerId = ps.playerId 
        WHERE ps.gameId = :gameId 
        ORDER BY COALESCE(ps.goals, 0) DESC
    """)
    fun getTopScorers(gameId: Long): Flow<List<PlayerWithStats>>

    // Asist krallığı sıralaması
    @Transaction
    @Query("""
        SELECT p.*, ps.*
        FROM players p 
        INNER JOIN player_stats ps ON p.playerId = ps.playerId 
        WHERE ps.gameId = :gameId 
        ORDER BY ps.assists DESC
    """)
    fun getTopAssists(gameId: Long): Flow<List<PlayerWithStats>>

    // En çok kart gören oyuncular
    @Transaction
    @Query("""
        SELECT p.*, ps.*
        FROM players p 
        INNER JOIN player_stats ps ON p.playerId = ps.playerId 
        WHERE ps.gameId = :gameId 
        ORDER BY (ps.yellowCards + ps.redCards * 2) DESC
    """)
    fun getMostBookedPlayers(gameId: Long): Flow<List<PlayerWithStats>>

    // En çok gol yemeden maç bitiren kaleciler
    @Transaction
    @Query("""
        SELECT p.*, ps.*
        FROM players p 
        INNER JOIN player_stats ps ON p.playerId = ps.playerId 
        WHERE ps.gameId = :gameId AND p.position = 'Goalkeeper'
        ORDER BY ps.cleanSheets DESC
    """)
    fun getTopCleanSheets(gameId: Long): Flow<List<PlayerWithStats>>

    // Eğer kayıt yoksa yeni kayıt oluştur
    @Query("SELECT EXISTS(SELECT 1 FROM player_stats WHERE playerId = :playerId AND gameId = :gameId)")
    suspend fun hasStats(playerId: Long, gameId: Long): Boolean

    @Insert
    suspend fun insertInitialStats(stats: PlayerStats)

    @Transaction
    suspend fun createStatsIfNotExists(playerId: Long, gameId: Long) {
        Log.d("PlayerStatsDao", "Checking stats for player $playerId in game $gameId")
        if (!hasStats(playerId, gameId)) {
            Log.d("PlayerStatsDao", "Creating new stats for player $playerId")
            val initialStats = PlayerStats(
                playerId = playerId,
                gameId = gameId,
                appearances = 0,
                goals = 0,
                assists = 0,
                cleanSheets = 0,
                yellowCards = 0,
                redCards = 0
            )
            insertInitialStats(initialStats)
            Log.d("PlayerStatsDao", "Initial stats created successfully")
        }
    }

    // İstatistikleri güncelle
    @Query("""
        UPDATE player_stats 
        SET goals = COALESCE(goals, 0) + :goals,
            assists = COALESCE(assists, 0) + :assists
        WHERE playerId = :playerId AND gameId = :gameId
    """)
    suspend fun updateMatchStats(playerId: Long, gameId: Long, goals: Int, assists: Int)

    // Maça çıkma sayısını artır
    @Query("""
        UPDATE player_stats 
        SET appearances = COALESCE(appearances, 0) + 1
        WHERE playerId = :playerId AND gameId = :gameId
    """)
    suspend fun incrementAppearance(playerId: Long, gameId: Long)

    // Oyuncuları ve istatistiklerini birlikte al
    @Transaction
    @Query("""
        SELECT p.*, ps.*
        FROM players p
        LEFT JOIN player_stats ps ON p.playerId = ps.playerId AND ps.gameId = :gameId
        WHERE p.teamId = :teamId
        ORDER BY COALESCE(ps.goals, 0) DESC, COALESCE(ps.assists, 0) DESC
    """)
    fun getPlayerStatsWithPlayers(teamId: Long, gameId: Long): Flow<List<PlayerWithStats>>

    // Clean sheet sayısını artır
    @Query("""
        UPDATE player_stats 
        SET cleanSheets = COALESCE(cleanSheets, 0) + 1
        WHERE playerId = :playerId AND gameId = :gameId
    """)
    suspend fun updateCleanSheet(playerId: Long, gameId: Long)

    @Query("""
        UPDATE player_stats 
        SET appearances = appearances + 1 
        WHERE playerId = :playerId AND gameId = :gameId
    """)
    suspend fun updateAppearances(playerId: Long, gameId: Long)

    data class PlayerWithStats(
        @Embedded val player: Player,
        @Relation(
            parentColumn = "playerId",
            entityColumn = "playerId",
            entity = PlayerStats::class
        )
        val stats: PlayerStats?
    )
} 