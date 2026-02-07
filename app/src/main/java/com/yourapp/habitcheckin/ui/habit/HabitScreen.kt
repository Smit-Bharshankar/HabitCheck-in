package com.yourapp.habitcheckin.ui.habit

import androidx.compose.foundation.background
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun HabitScreen(
    modifier: Modifier = Modifier,
    habitName: String,
    todayLabel: String,
    isCompletedToday: Boolean,
    weekProgress: List<DayProgress>,
    onCheckIn: () -> Unit
) {
    val isCompleted = isCompletedToday
    val statusText = if (isCompleted) "Completed" else "Pending"

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF0B1020), Color(0xFF141B2E), Color(0xFF1A1233))
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
                    containerColor = Color(0xFF1A2742)
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
                        color = Color(0xFFB6E3FF)
                    )
                    Text(
                        text = todayLabel,
                        style = MaterialTheme.typography.bodyLarge,
                        color = Color(0xFF8BC8FF)
                    )
                }
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 14.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = Color(0xFF12162A)
                )
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(24.dp))
                        .background(
                            brush = Brush.linearGradient(
                                colors = listOf(Color(0xFF173A5A), Color(0xFF4C1D95), Color(0xFF0F8B5F))
                            )
                        )
                        .padding(horizontal = 22.dp, vertical = 26.dp)
                ) {
                    Text(
                        text = habitName,
                        style = MaterialTheme.typography.headlineMedium,
                        color = Color(0xFFEAF7FF),
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.Start) {
                Surface(
                    shape = RoundedCornerShape(50),
                    color = if (isCompleted) Color(0xFF0F8B5F) else Color(0xFFB26B00),
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
                        containerColor = Color(0xFF3D5AFE),
                        contentColor = Color.White,
                        disabledContainerColor = Color(0xFF2A335E),
                        disabledContentColor = Color(0xFF9EA7D8)
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
                                    Color(0xFF7AA2FF)
                                } else {
                                    Color(0xFF4A4F62)
                                }
                            )
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))
        }
    }
}
