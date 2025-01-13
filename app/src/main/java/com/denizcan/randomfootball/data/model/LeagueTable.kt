package com.denizcan.randomfootball.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "league_tables",
    foreignKeys = [
        ForeignKey(
            entity = League::class,
            parentColumns = ["leagueId"],
            childColumns = ["leagueId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Team::class,
            parentColumns = ["teamId"],
            childColumns = ["teamId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["leagueId"]),
        Index(value = ["teamId"]),
        Index(value = ["leagueId", "teamId"], unique = true)
    ]
)
data class LeagueTable(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val leagueId: Long,
    val teamId: Long,
    val position: Int = 0,      // Sıralama
    val points: Int = 0,        // Puan
    val played: Int = 0,        // Oynadığı maç
    val won: Int = 0,           // Galibiyet
    val drawn: Int = 0,         // Beraberlik
    val lost: Int = 0,          // Mağlubiyet
    val goalsFor: Int = 0,      // Attığı gol
    val goalsAgainst: Int = 0,  // Yediği gol
    val goalDifference: Int = 0, // Averaj
    val form: String = ""       // Son 5 maç (örn: "WWDLL")
) 