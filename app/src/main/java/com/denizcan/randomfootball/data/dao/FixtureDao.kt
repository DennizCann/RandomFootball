package com.denizcan.randomfootball.data.dao

import androidx.room.*
import com.denizcan.randomfootball.data.model.Fixture
import kotlinx.coroutines.flow.Flow

@Dao
interface FixtureDao {
    @Insert
    suspend fun insertFixture(fixture: Fixture): Long

    @Insert
    suspend fun insertFixtures(fixtures: List<Fixture>)

    @Update
    suspend fun updateFixture(fixture: Fixture)

    @Delete
    suspend fun deleteFixture(fixture: Fixture)

    @Query("""
        SELECT * FROM fixtures 
        WHERE gameId = :gameId AND leagueId = :leagueId 
        ORDER BY week ASC, fixtureId ASC
    """)
    fun getFixturesByLeague(gameId: Long, leagueId: Long): Flow<List<Fixture>>

    @Query("SELECT * FROM fixtures WHERE gameId = :gameId AND leagueId = :leagueId AND isPlayed = 0 ORDER BY week ASC LIMIT 1")
    fun getNextFixture(gameId: Long, leagueId: Long): Flow<Fixture?>

    @Query("DELETE FROM fixtures WHERE gameId = :gameId")
    suspend fun deleteFixturesByGameId(gameId: Long)

    @Query("SELECT COUNT(*) FROM fixtures WHERE leagueId = :leagueId")
    suspend fun getFixtureCountForLeague(leagueId: Long): Int
} 