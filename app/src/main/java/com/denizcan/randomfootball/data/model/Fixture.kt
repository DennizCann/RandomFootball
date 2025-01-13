package com.denizcan.randomfootball.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.Index

@Entity(
    tableName = "fixtures",
    foreignKeys = [
        ForeignKey(
            entity = Game::class,
            parentColumns = ["gameId"],
            childColumns = ["gameId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = League::class,
            parentColumns = ["leagueId"],
            childColumns = ["leagueId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Team::class,
            parentColumns = ["teamId"],
            childColumns = ["homeTeamId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Team::class,
            parentColumns = ["teamId"],
            childColumns = ["awayTeamId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["gameId"]),
        Index(value = ["leagueId"]),
        Index(value = ["homeTeamId"]),
        Index(value = ["awayTeamId"])
    ]
)
data class Fixture(
    @PrimaryKey(autoGenerate = true)
    val fixtureId: Long = 0,
    val gameId: Long,
    val leagueId: Long,
    val homeTeamId: Long,
    val awayTeamId: Long,
    val week: Int,
    val homeScore: Int = 0,
    val awayScore: Int = 0,
    val isPlayed: Boolean = false
) 