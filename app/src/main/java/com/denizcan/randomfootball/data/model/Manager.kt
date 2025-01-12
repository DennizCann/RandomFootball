package com.denizcan.randomfootball.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "managers",
    foreignKeys = [
        ForeignKey(
            entity = Team::class,
            parentColumns = ["teamId"],
            childColumns = ["teamId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class Manager(
    @PrimaryKey(autoGenerate = true) val managerId: Long = 0,
    val teamId: Long,
    val name: String,
    val nationality: String,
    val formation: String
)
