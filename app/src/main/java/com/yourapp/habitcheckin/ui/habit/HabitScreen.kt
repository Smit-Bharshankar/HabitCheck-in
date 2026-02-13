package com.yourapp.habitcheckin.ui.habit

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.RestartAlt
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

private val ScreenBackgroundTop = Color(0xFF101522)
private val ScreenBackgroundMid = Color(0xFF151D30)
private val ScreenBackgroundBottom = Color(0xFF1A1F33)
private val HeaderCardColor = Color(0xFF1D2A40)
private val HeaderLabelColor = Color(0xFFC6DBFF)
private val HeaderDateColor = Color(0xFF9FBFF6)
private val HabitCardBaseColor = Color(0xFF141A2A)
private val HabitCardGradientStart = Color(0xFF1D537E)
private val HabitCardGradientMid = Color(0xFF44408D)
private val HabitCardGradientEnd = Color(0xFF1D7A72)
private val HabitTitleColor = Color(0xFFE8EEFF)
private val CompletedPillColor = Color(0xFF3AB28C)
private val PendingPillColor = Color(0xFF5F6578)
private val CheckInButtonColor = Color(0xFF5875E7)
private val CheckInButtonPressedColor = Color(0xFF4965D1)
private val DisabledButtonColor = Color(0xFF2A335E)
private val DisabledButtonTextColor = Color(0xFF9EA7D8)
private val ProgressDotCompletedColor = Color(0xFF84A7FF)
private val ProgressDotEmptyColor = Color(0xFF4B5266)
private val MenuActionColor = Color(0xFFE8ECF4)
private val DestructiveColor = Color(0xFFFF5252)
private val MenuContainerColor = Color(0xFF1E2128)
private val MenuBorderColor = Color(0xFF414756)
private val MenuDividerColor = Color(0xFF353A45)
private const val HabitNameMaxLength = 60
private const val maxChar = 60


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HabitScreen(
    modifier: Modifier = Modifier,
    habitName: String,
    todayLabel: String,
    isCompletedToday: Boolean,
    weekProgress: List<DayProgress>,
    intentDraft: String,
    isIntentInputExpanded: Boolean,
    onCheckIn: () -> Unit,
    onEditName: (String) -> Unit,
    onUndoToday: () -> Unit,
    onRemoveHabit: () -> Unit,
    onIntentPromptTapped: () -> Unit,
    onIntentChanged: (String) -> Unit,
    onCollapseIntentInput: () -> Unit
) {
    val isCompleted = isCompletedToday
    val statusText = if (isCompleted) "You showed up." else "Ready when you are."
    var showMenu by remember { mutableStateOf(false) }
    var showEditDialog by remember { mutableStateOf(false) }
    var editName by remember(habitName) { mutableStateOf(habitName) }
    var isIntentFieldFocused by remember { mutableStateOf(false) }
    val haptic = LocalHapticFeedback.current
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    val animatedButtonScale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = tween(durationMillis = 120),
        label = "checkin_button_scale"
    )
    val animatedButtonColor = androidx.compose.animation.animateColorAsState(
        targetValue = when {
            isCompleted -> DisabledButtonColor
            isPressed -> CheckInButtonPressedColor
            else -> CheckInButtonColor
        },
        animationSpec = tween(durationMillis = 180),
        label = "checkin_button_color"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .imePadding()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(ScreenBackgroundTop, ScreenBackgroundMid, ScreenBackgroundBottom)
                )
            )
            .padding(20.dp)
    ) {
        Column(
            modifier = Modifier.verticalScroll(rememberScrollState())
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 14.dp),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(
                    containerColor = HeaderCardColor
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 18.dp, vertical = 16.dp)
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
                    .padding(bottom = 16.dp),
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
                            .padding(horizontal = 22.dp, vertical = 30.dp)
                    ) {
                        Text(
                            text = habitName,
                            style = MaterialTheme.typography.headlineLarge,
                            color = HabitTitleColor,
                            fontWeight = FontWeight.Medium,
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
                    modifier = Modifier.height(42.dp)
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
                AnimatedVisibility(
                    visible = !isCompleted,
                    enter = fadeIn() + expandHorizontally(),
                    exit = fadeOut() + shrinkHorizontally()
                ) {
                    Button(
                        onClick = {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            onCheckIn()
                        },
                        enabled = !isCompleted,
                        interactionSource = interactionSource,
                        shape = RoundedCornerShape(50),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = animatedButtonColor.value,
                            contentColor = Color.White,
                            disabledContainerColor = DisabledButtonColor,
                            disabledContentColor = DisabledButtonTextColor
                        ),
                        modifier = Modifier
                            .height(42.dp)
                            .scale(animatedButtonScale)
                    ) {
                        Text(
                            text = "Check In",
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                weekProgress.forEach { day ->
                    val dotScale by animateFloatAsState(
                        targetValue = if (day.isCompleted) 1f else 0.84f,
                        animationSpec = tween(durationMillis = 260),
                        label = "dot_scale"
                    )
                    val dotAlpha by animateFloatAsState(
                        targetValue = if (day.isCompleted) 1f else 0.58f,
                        animationSpec = tween(durationMillis = 260),
                        label = "dot_alpha"
                    )
                    Box(
                        modifier = Modifier
                            .size(12.dp)
                            .scale(dotScale)
                            .alpha(dotAlpha)
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

            Spacer(modifier = Modifier.height(22.dp))

            TextButton(
                onClick = {
                    if (isIntentInputExpanded) {
                        onCollapseIntentInput()
                        isIntentFieldFocused = false
                    } else {
                        onIntentPromptTapped()
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 6.dp),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(
                    horizontal = 8.dp,
                    vertical = 10.dp
                )
            ) {
                Row(
                    horizontalArrangement = Arrangement.Start,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Why are you showing up today?",
                        style = MaterialTheme.typography.bodyMedium,
                        color = HeaderDateColor
                    )
                    Icon(
                        imageVector = if (!isIntentInputExpanded) Icons.Filled.ArrowDropDown else Icons.Filled.ArrowDropUp,
                        contentDescription = null,
                        tint = HeaderDateColor
                    )
                }
            }

            AnimatedVisibility(
                visible = isIntentInputExpanded,
                enter = fadeIn(animationSpec = tween(140)) + expandVertically(animationSpec = tween(140)),
                exit = fadeOut(animationSpec = tween(120)) + shrinkVertically(animationSpec = tween(120))
            ) {
                Column(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = intentDraft,
                        onValueChange = {
                            if (it.length <= maxChar) onIntentChanged(it)
                        },
                        minLines = 2,
                        maxLines = 4,
                        singleLine = false,
                        shape = RoundedCornerShape(12.dp),
                        placeholder = { Text("(Optional) Share what brought you here today.") },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(104.dp)
                            .onFocusChanged { isIntentFieldFocused = it.isFocused },
                        supportingText = {
                            if (isIntentFieldFocused) {
                                Text(
                                    text = "${intentDraft.length} / $maxChar",
                                    modifier = Modifier.fillMaxWidth(),
                                    textAlign = TextAlign.End,
                                )
                            }
                        }
                    )

                    AnimatedVisibility(
                        visible = isIntentFieldFocused,
                        enter = fadeIn(animationSpec = tween(100)),
                        exit = fadeOut(animationSpec = tween(100))
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 4.dp),
                            horizontalArrangement = Arrangement.End
                        ) {
                            TextButton(
                                onClick = {
                                    onCollapseIntentInput()
                                    isIntentFieldFocused = false
                                }
                            ) {
                                Text("Cancel")
                            }
                            Spacer(modifier = Modifier.width(4.dp))
                            TextButton(
                                onClick = {
                                    onCollapseIntentInput()
                                    isIntentFieldFocused = false
                                }
                            ) {
                                Text("Submit")
                            }
                        }
                    }
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
                    onValueChange = { editName = it.take(HabitNameMaxLength) },
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
