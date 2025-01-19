package com.denizcan.randomfootball.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.denizcan.randomfootball.data.converter.Converters
import com.denizcan.randomfootball.data.dao.*
import com.denizcan.randomfootball.data.model.*

@Database(
    entities = [
        Game::class,
        League::class,
        Team::class,
        Manager::class,
        Player::class,
        Fixture::class,
        LeagueTable::class,
        PlayerStats::class
    ],
    version = 17,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun gameDao(): GameDao
    abstract fun leagueDao(): LeagueDao
    abstract fun teamDao(): TeamDao
    abstract fun managerDao(): ManagerDao
    abstract fun playerDao(): PlayerDao
    abstract fun fixtureDao(): FixtureDao
    abstract fun leagueTableDao(): LeagueTableDao
    abstract fun playerStatsDao(): PlayerStatsDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "football_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
