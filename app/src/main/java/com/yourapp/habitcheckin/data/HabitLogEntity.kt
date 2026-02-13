package com.yourapp.habitcheckin.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "habit_log",
    indices = [Index(value = ["habitId", "date"], unique = true)]
)
data class HabitLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val habitId: Int,
    val date: String,
    val intent: String?
)
