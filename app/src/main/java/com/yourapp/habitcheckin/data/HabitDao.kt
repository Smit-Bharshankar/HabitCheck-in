package com.yourapp.habitcheckin.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface HabitDao {
    @Query("SELECT * FROM habit WHERE isArchived = 0 ORDER BY createdAt ASC, id ASC")
    suspend fun getAllHabits(): List<HabitEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertHabit(habit: HabitEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabits(habits: List<HabitEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHabit(habit: HabitEntity)

    @Query("SELECT COALESCE(MAX(id), 0) + 1 FROM habit")
    suspend fun getNextHabitId(): Int

    @Query("SELECT EXISTS(SELECT 1 FROM habit_log WHERE habitId = :habitId AND date = :date)")
    suspend fun hasLogForDate(habitId: Int, date: String): Boolean

    @Query("SELECT MIN(date) FROM habit_log WHERE habitId = :habitId")
    suspend fun getEarliestLogDate(habitId: Int): String?

    @Query(
        "SELECT date FROM habit_log WHERE habitId = :habitId AND date BETWEEN :startDate AND :endDate"
    )
    suspend fun getLogDatesInRange(habitId: Int, startDate: String, endDate: String): List<String>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertHabitLog(log: HabitLogEntity): Long

    @Query("UPDATE habit_log SET intent = :intent WHERE habitId = :habitId AND date = :date")
    suspend fun updateHabitLogIntentForDate(habitId: Int, date: String, intent: String?)

    @Query("UPDATE habit SET name = :name WHERE id = :habitId")
    suspend fun updateHabitName(habitId: Int, name: String)

    @Query("UPDATE habit SET isArchived = 1 WHERE id = :habitId")
    suspend fun archiveHabit(habitId: Int)

    @Query("DELETE FROM habit_log WHERE habitId = :habitId AND date = :date")
    suspend fun deleteHabitLogForDate(habitId: Int, date: String)
}
