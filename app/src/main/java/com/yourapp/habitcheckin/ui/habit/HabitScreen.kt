package com.yourapp.habitcheckin.ui.habit

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.TextButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.RestartAlt
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

private val ScreenBackgroundTop = Color(0xFF0B1020)
private val ScreenBackgroundMid = Color(0xFF141B2E)
private val ScreenBackgroundBottom = Color(0xFF1A1233)
private val HeaderCardColor = Color(0xFF1A2742)
private val HeaderLabelColor = Color(0xFFB6E3FF)
private val HeaderDateColor = Color(0xFF8BC8FF)
private val HabitCardBaseColor = Color(0xFF12162A)
private val HabitCardGradientStart = Color(0xFF173A5A)
private val HabitCardGradientMid = Color(0xFF4C1D95)
private val HabitCardGradientEnd = Color(0xFF0F8B5F)
private val HabitTitleColor = Color(0xFFEAF7FF)
private val CompletedPillColor = Color(0xFF0F8B5F)
private val PendingPillColor = Color(0xFFB26B00)
private val CheckInButtonColor = Color(0xFF3D5AFE)
private val DisabledButtonColor = Color(0xFF2A335E)
private val DisabledButtonTextColor = Color(0xFF9EA7D8)
private val ProgressDotCompletedColor = Color(0xFF7AA2FF)
private val ProgressDotEmptyColor = Color(0xFF4A4F62)
private val MenuActionColor = Color(0xFFE8ECF4)
private val DestructiveColor = Color(0xFFFF5252)
private val MenuContainerColor = Color(0xFF1E2128)
private val MenuBorderColor = Color(0xFF414756)
private val MenuDividerColor = Color(0xFF353A45)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HabitScreen(
    modifier: Modifier = Modifier,
    habitName: String,
    todayLabel: String,
    isCompletedToday: Boolean,
    weekProgress: List<DayProgress>,
    onCheckIn: () -> Unit,
    onEditName: (String) -> Unit,
    onUndoToday: () -> Unit,
    onRemoveHabit: () -> Unit
) {
    val isCompleted = isCompletedToday
    val statusText = if (isCompleted) "Completed" else "Pending"
    var showMenu by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var editName by remember(habitName) { mutableStateOf(habitName) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(ScreenBackgroundTop, ScreenBackgroundMid, ScreenBackgroundBottom)
                )
            )
            .padding(20.dp)
    ) {
        Column {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = HeaderCardColor
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 14.dp)
                ) {
                    Text(
                        text = "Today",
                        style = MaterialTheme.typography.labelLarge,
                        color = HeaderLabelColor
                    )
                    Text(
                        text = todayLabel,
                        style = MaterialTheme.typography.bodyLarge,
                        color = HeaderDateColor
                    )
                }
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 14.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = HabitCardBaseColor
                )
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(24.dp))
                            .background(
                                brush = Brush.linearGradient(
                                    colors = listOf(HabitCardGradientStart, HabitCardGradientMid, HabitCardGradientEnd)
                                )
                            )
                            .combinedClickable(
                                onClick = {},
                                onLongClick = { showMenu = true }
                            )
                            .padding(horizontal = 22.dp, vertical = 26.dp)
                    ) {
                        Text(
                            text = habitName,
                            style = MaterialTheme.typography.headlineMedium,
                            color = HabitTitleColor,
                            fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    MaterialTheme(
                        shapes = MaterialTheme.shapes.copy(extraSmall = RoundedCornerShape(16.dp))
                    ) {
                        DropdownMenu(
                            expanded = showMenu,
                            onDismissRequest = { showMenu = false },
                            containerColor = MenuContainerColor,
                            modifier = Modifier
                                .width(220.dp)
                                .border(0.5.dp, MenuBorderColor, RoundedCornerShape(16.dp))
                        ) {
                            DropdownMenuItem(
                                text = { Text("Edit habit", style = MaterialTheme.typography.bodyLarge) },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Outlined.Edit,
                                        contentDescription = null,
                                        tint = MenuActionColor
                                    )
                                },
                                onClick = {
                                    showMenu = false
                                    editName = habitName
                                    showEditDialog = true
                                }
                            )

                            if (isCompleted) {
                                HorizontalDivider(
                                    modifier = Modifier.padding(horizontal = 12.dp),
                                    color = MenuDividerColor
                                )
                                DropdownMenuItem(
                                    text = { Text("Undo check-in", style = MaterialTheme.typography.bodyLarge) },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Outlined.RestartAlt,
                                            contentDescription = null,
                                            tint = MenuActionColor
                                        )
                                    },
                                    onClick = {
                                        showMenu = false
                                        onUndoToday()
                                    }
                                )
                            }

                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 12.dp),
                                color = MenuDividerColor
                            )
                            DropdownMenuItem(
                                text = { Text("Delete habit", style = MaterialTheme.typography.bodyLarge) },
                                leadingIcon = {
                                    Icon(
                                        imageVector = Icons.Outlined.Delete,
                                        contentDescription = null,
                                        tint = DestructiveColor
                                    )
                                },
                                onClick = {
                                    showMenu = false
                                    onRemoveHabit()
                                }
                            )
                        }
                    }
                }
            }

            Row(horizontalArrangement = Arrangement.Start) {
                Surface(
                    shape = RoundedCornerShape(50),
                    color = if (isCompleted) CompletedPillColor else PendingPillColor,
                    modifier = Modifier.height(40.dp)
                ) {
                    Text(
                        text = statusText,
                        color = Color.White,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.padding(horizontal = 18.dp, vertical = 10.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Button(
                    onClick = onCheckIn,
                    enabled = !isCompleted,
                    shape = RoundedCornerShape(50),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CheckInButtonColor,
                        contentColor = Color.White,
                        disabledContainerColor = DisabledButtonColor,
                        disabledContentColor = DisabledButtonTextColor
                    ),
                    modifier = Modifier.height(40.dp)
                ) {
                    Text(
                        text = if (isCompleted) "Checked In" else "Check In",
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                weekProgress.forEach { day ->
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .clip(RoundedCornerShape(50))
                            .background(
                                color = if (day.isCompleted) {
                                    ProgressDotCompletedColor
                                } else {
                                    ProgressDotEmptyColor
                                }
                            )
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))
        }
    }

    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Edit name") },
            text = {
                OutlinedTextField(
                    value = editName,
                    onValueChange = { editName = it },
                    label = { Text("Habit name") },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onEditName(editName)
                        showEditDialog = false
                    },
                    enabled = editName.trim().isNotEmpty()
                ) {
                    Text("Save")
                }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }
}
