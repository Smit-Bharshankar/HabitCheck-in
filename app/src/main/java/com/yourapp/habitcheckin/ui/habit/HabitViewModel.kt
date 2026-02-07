package com.yourapp.habitcheckin.ui.habit

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.yourapp.habitcheckin.data.HabitDatabase
import com.yourapp.habitcheckin.data.HabitEntity
import com.yourapp.habitcheckin.data.HabitLogEntity
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeFormatter

data class DayProgress(
    val date: LocalDate,
    val isCompleted: Boolean
)

data class HabitPageState(
    val habitId: Int,
    val habitName: String,
    val isCompletedToday: Boolean,
    val weekProgress: List<DayProgress>
)

class HabitViewModel(application: Application) : AndroidViewModel(application) {
    private val habitDao = HabitDatabase.getInstance(application).habitDao()
    private val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("EEE, MMM d")

    var habitPages by mutableStateOf<List<HabitPageState>>(emptyList())
        private set

    val today: LocalDate
        get() = LocalDate.now()

    val todayLabel: String
        get() = today.format(formatter)

    init {
        viewModelScope.launch {
            refreshHabitPages()
        }
    }

    fun onCheckIn(habitId: Int) {
        viewModelScope.launch {
            if (habitDao.hasLogForDate(habitId, today.toString())) return@launch
            habitDao.insertHabitLog(
                HabitLogEntity(
                    habitId = habitId,
                    date = today.toString()
                )
            )
            refreshHabitPages()
        }
    }

    fun addHabit(name: String) {
        val trimmedName = name.trim()
        if (trimmedName.isEmpty()) return

        viewModelScope.launch {
            val nextId = habitDao.getNextHabitId()
            habitDao.insertHabit(
                HabitEntity(
                    id = nextId,
                    name = trimmedName,
                    createdAt = today.toString(),
                    isArchived = false
                )
            )
            refreshHabitPages()
        }
    }

    private suspend fun refreshHabitPages() {
        val habits = habitDao.getAllHabits()
        habitPages = habits.map { habit ->
            HabitPageState(
                habitId = habit.id,
                habitName = habit.name,
                isCompletedToday = habitDao.hasLogForDate(habit.id, today.toString()),
                weekProgress = loadWeekProgress(habit.id)
            )
        }
    }

    private suspend fun loadWeekProgress(habitId: Int): List<DayProgress> {
        val end = today
        val earliestCompletedDate = parseDate(habitDao.getEarliestLogDate(habitId))
        val dynamicStart = earliestCompletedDate ?: end
        val start = if (dynamicStart.isBefore(end.minusDays(6))) end.minusDays(6) else dynamicStart
        val completedDates = habitDao.getLogDatesInRange(
            habitId = habitId,
            startDate = start.toString(),
            endDate = end.toString()
        ).toSet()

        return (0L..end.toEpochDay() - start.toEpochDay()).map { offset ->
            val date = start.plusDays(offset)
            DayProgress(
                date = date,
                isCompleted = completedDates.contains(date.toString())
            )
        }
    }

    private fun parseDate(value: String?): LocalDate? {
        if (value == null) return null
        return runCatching { LocalDate.parse(value) }.getOrNull()
    }
}
