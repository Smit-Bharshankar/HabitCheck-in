package com.yourapp.habitcheckin.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "habit")
data class HabitEntity(
    @PrimaryKey val id: Int = 1,
    val name: String,
    val createdAt: String,
    val isArchived: Boolean
)
