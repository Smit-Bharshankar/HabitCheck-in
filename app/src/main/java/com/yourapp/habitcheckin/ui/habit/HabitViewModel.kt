package com.yourapp.habitcheckin.ui.habit

import android.app.Application
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
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

private const val HabitNameMaxLength = 60

data class DayProgress(
    val date: LocalDate,
    val isCompleted: Boolean
)

data class HabitPageState(
    val habitId: Int,
    val habitName: String,
    val isCompletedToday: Boolean,
    val weekProgress: List<DayProgress>,
    val intentDraft: String,
    val isIntentInputExpanded: Boolean
)

class HabitViewModel(application: Application) : AndroidViewModel(application) {
    private val habitDao = HabitDatabase.getInstance(application).habitDao()
    private val formatter: DateTimeFormatter = DateTimeFormatter.ofPattern("EEE, MMM d")
    private val intentDrafts = mutableStateMapOf<Int, String>()
    private val expandedIntentInputs = mutableStateMapOf<Int, Boolean>()

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
            val intentToSave = intentDrafts[habitId]
                ?.trim()
                ?.takeIf { it.isNotEmpty() }

            val rowId = habitDao.insertHabitLog(
                HabitLogEntity(
                    habitId = habitId,
                    date = today.toString(),
                    intent = intentToSave
                )
            )
            if (rowId != -1L) {
                refreshHabitPages()
            }
        }
    }

    fun onSubmitIntent(habitId: Int) {
        viewModelScope.launch {
            if (!habitDao.hasLogForDate(habitId, today.toString())) return@launch
            val intentToSave = intentDrafts[habitId]
                ?.trim()
                ?.takeIf { it.isNotEmpty() }
            habitDao.updateHabitLogIntentForDate(
                habitId = habitId,
                date = today.toString(),
                intent = intentToSave
            )
            refreshHabitPages()
        }
    }

    fun onIntentPromptTapped(habitId: Int) {
        expandedIntentInputs[habitId] = true
        refreshHabitPagesFromMemory()
    }

    fun onIntentChanged(habitId: Int, value: String) {
        intentDrafts[habitId] = value.take(120)
        refreshHabitPagesFromMemory()
    }

    fun collapseIntentInput(habitId: Int) {
        expandedIntentInputs[habitId] = false
        refreshHabitPagesFromMemory()
    }

    fun onHabitPageVisible(habitId: Int) {
        val keys = expandedIntentInputs.keys.toList()
        keys.forEach { id ->
            if (id != habitId) expandedIntentInputs[id] = false
        }
        refreshHabitPagesFromMemory()
    }

    fun addHabit(name: String) {
        val trimmedName = name.trim().take(HabitNameMaxLength)
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

    fun editHabitName(habitId: Int, name: String) {
        val trimmedName = name.trim().take(HabitNameMaxLength)
        if (trimmedName.isEmpty()) return

        viewModelScope.launch {
            habitDao.updateHabitName(habitId, trimmedName)
            refreshHabitPages()
        }
    }

    fun undoToday(habitId: Int) {
        viewModelScope.launch {
            habitDao.deleteHabitLogForDate(habitId, today.toString())
            refreshHabitPages()
        }
    }

    fun removeHabit(habitId: Int) {
        viewModelScope.launch {
            habitDao.archiveHabit(habitId)
            refreshHabitPages()
        }
    }

    private suspend fun refreshHabitPages() {
        val habits = habitDao.getAllHabits()
        val validIds = habits.map { it.id }.toSet()
        intentDrafts.keys.toList().forEach { if (!validIds.contains(it)) intentDrafts.remove(it) }
        expandedIntentInputs.keys.toList().forEach { if (!validIds.contains(it)) expandedIntentInputs.remove(it) }

        habitPages = habits.map { habit ->
            HabitPageState(
                habitId = habit.id,
                habitName = habit.name,
                isCompletedToday = habitDao.hasLogForDate(habit.id, today.toString()),
                weekProgress = loadWeekProgress(habit.id),
                intentDraft = intentDrafts[habit.id].orEmpty(),
                isIntentInputExpanded = expandedIntentInputs[habit.id] == true
            )
        }
    }

    private fun refreshHabitPagesFromMemory() {
        habitPages = habitPages.map { page ->
            page.copy(
                intentDraft = intentDrafts[page.habitId].orEmpty(),
                isIntentInputExpanded = expandedIntentInputs[page.habitId] == true
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
