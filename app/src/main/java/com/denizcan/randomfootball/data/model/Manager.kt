package com.denizcan.randomfootball.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.Index

@Entity(
    tableName = "managers",
    foreignKeys = [
        ForeignKey(
            entity = Team::class,
            parentColumns = ["teamId"],
            childColumns = ["teamId"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = Game::class,
            parentColumns = ["gameId"],
            childColumns = ["gameId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("teamId"), Index("gameId")]
)
data class Manager(
    @PrimaryKey(autoGenerate = true)
    val managerId: Long = 0,
    val name: String,
    val teamId: Long,
    val gameId: Long,
    val formation: String = "4-4-2",
    val nationality: String
)
