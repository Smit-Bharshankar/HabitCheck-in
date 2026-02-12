package com.yourapp.habitcheckin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.yourapp.habitcheckin.ui.habit.HabitScreen
import com.yourapp.habitcheckin.ui.habit.HabitViewModel
import com.yourapp.habitcheckin.ui.theme.HabitCheckinTheme
import kotlin.math.absoluteValue

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            App()
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun App() {
    val habitViewModel: HabitViewModel = viewModel()
    val habitPages = habitViewModel.habitPages
    var showAddDialog by remember { mutableStateOf(false) }
    var showInfoDialog by remember { mutableStateOf(false) }
    val pagerState = rememberPagerState(pageCount = { habitPages.size })

    LaunchedEffect(habitPages.size) {
        if (habitPages.isNotEmpty() && pagerState.currentPage > habitPages.lastIndex) {
            pagerState.scrollToPage(habitPages.lastIndex)
        }
    }

    HabitCheckinTheme(darkTheme = true, dynamicColor = false) {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = {
                TopAppBar(
                    title = { Text("Habit Checkin") },
                    actions = {
                        TextButton(onClick = { showInfoDialog = true }) {
                            Text("i")
                        }
                    }
                )
            },
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { showAddDialog = true },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 8.dp)
                ) {
                    Text(text = "+", style = MaterialTheme.typography.headlineMedium)
                }
            }
        ) { innerPadding ->
            if (habitPages.isEmpty()) {
                EmptyState(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding),
                    onAddHabit = { showAddDialog = true }
                )
            } else {
                HorizontalPager(
                    state = pagerState,
                    userScrollEnabled = habitPages.size > 1,
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) { page ->
                    val habitPage = habitPages[page]
                    val pageOffset = ((pagerState.currentPage - page) + pagerState.currentPageOffsetFraction).absoluteValue
                    HabitScreen(
                        modifier = Modifier
                            .fillMaxSize()
                            .graphicsLayer {
                                translationX = pageOffset * 28f
                                alpha = 1f - (pageOffset * 0.16f)
                                scaleY = 1f - (pageOffset * 0.04f)
                            },
                        habitName = habitPage.habitName,
                        todayLabel = habitViewModel.todayLabel,
                        isCompletedToday = habitPage.isCompletedToday,
                        weekProgress = habitPage.weekProgress,
                        onCheckIn = { habitViewModel.onCheckIn(habitPage.habitId) },
                        onEditName = { name -> habitViewModel.editHabitName(habitPage.habitId, name) },
                        onUndoToday = { habitViewModel.undoToday(habitPage.habitId) },
                        onRemoveHabit = { habitViewModel.removeHabit(habitPage.habitId) }
                    )
                }
            }
        }

        if (showAddDialog) {
            AddHabitDialog(
                onDismiss = { showAddDialog = false },
                onSave = { name ->
                    habitViewModel.addHabit(name)
                    showAddDialog = false
                }
            )
        }

        if (showInfoDialog) {
            InfoDialog(onDismiss = { showInfoDialog = false })
        }
    }
}

@Composable
private fun EmptyState(modifier: Modifier = Modifier, onAddHabit: () -> Unit) {
    Column(
        modifier = modifier.padding(24.dp),
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "What do you want to show up for?",
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        Button(
            onClick = onAddHabit,
            modifier = Modifier
                .padding(top = 16.dp)
                .fillMaxWidth()
        ) {
            Text("+ Add your first habit")
        }
    }
}

@Composable
private fun AddHabitDialog(
    onDismiss: () -> Unit,
    onSave: (String) -> Unit
) {
    var habitName by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Column {
                Text("Add Habit", style = MaterialTheme.typography.headlineSmall)
                Spacer(modifier = Modifier.height(6.dp))
                Text(
                    "Name the one thing you want to show up for.",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        },
        text = {
            Column {
                OutlinedTextField(
                    value = habitName,
                    onValueChange = { habitName = it },
                    label = { Text("Habit name") },
                    placeholder = { Text("e.g. Read 10 pages") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(
                onClick = { onSave(habitName) },
                enabled = habitName.trim().isNotEmpty()
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

@Composable
private fun InfoDialog(onDismiss: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Habit Checkin") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    "Show up for what matters, one day at a time.",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text("No streaks.", style = MaterialTheme.typography.titleMedium)
                Text("No guilt.", style = MaterialTheme.typography.titleMedium)
                Text("Just showing up.", style = MaterialTheme.typography.titleMedium)
            }
        },
        confirmButton = {
            Button(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}

@Preview(showBackground = true)
@Composable
private fun AppPreview() {
    App()
}
