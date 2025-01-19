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
    indices = [
        Index(value = ["teamId"], unique = true),
        Index(value = ["gameId"])
    ]
)
data class Manager(
    @PrimaryKey(autoGenerate = true)
    val managerId: Long = 0,
    val teamId: Long,
    val gameId: Long,
    val name: String,
    val nationality: String,
    val formation: String = "4-4-2"
)
