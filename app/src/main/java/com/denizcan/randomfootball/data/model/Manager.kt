package com.denizcan.randomfootball.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(tableName = "managers")
data class Manager(
    @PrimaryKey(autoGenerate = true)
    val managerId: Long = 0,
    val name: String,
    val teamId: Long,
    val formation: String,
    val nationality: String
)
