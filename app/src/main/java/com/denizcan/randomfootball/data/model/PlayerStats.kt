package com.denizcan.randomfootball.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.Index

@Entity(
    tableName = "player_stats",
    foreignKeys = [
        ForeignKey(
            entity = Player::class,
            parentColumns = ["playerId"],
            childColumns = ["playerId"]
        ),
        ForeignKey(
            entity = Game::class,
            parentColumns = ["gameId"],
            childColumns = ["gameId"]
        )
    ],
    primaryKeys = ["playerId", "gameId"]
)
data class PlayerStats(
    val playerId: Long,
    val gameId: Long,
    val appearances: Int = 0,
    val goals: Int = 0,
    val assists: Int = 0,
    val cleanSheets: Int = 0,
    val yellowCards: Int = 0,
    val redCards: Int = 0
)