@file:OptIn(ExperimentalMaterial3Api::class)

package org.example

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.*
@Composable
fun DashboardScreen(
    goals: List<Goal>,
    habits: List<Habit>,
    dailyLogs: List<DailyLog>,
    onNavigateToGoals: () -> Unit,
    onNavigateToHabits: () -> Unit
) {
    val scrollState = rememberScrollState()
    val today = SimpleDateFormat("EEEE, MMMM d", Locale.getDefault()).format(Date())

    // Calculate stats
    val activeGoals = goals.filter { !it.isCompleted }
    val completedGoals = goals.filter { it.isCompleted }
    val todayLogs = dailyLogs.filter {
        val logDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(it.date))
        val todayDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        logDate == todayDate
    }
    val currentStreak = habits.maxOfOrNull { it.streak } ?: 0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(32.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    "Welcome Back! 👋",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    today,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                IconButton(onClick = { }) {
                    Icon(Icons.Outlined.Notifications, "Notifications")
                }
                IconButton(onClick = { }) {
                    Icon(Icons.Outlined.Search, "Search")
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Stats Cards Row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            StatCard(
                modifier = Modifier.weight(1f),
                title = "Active Goals",
                value = activeGoals.size.toString(),
                icon = Icons.Outlined.Flag,
                gradient = listOf(Color(0xFF6366F1), Color(0xFF8B5CF6))
            )
            StatCard(
                modifier = Modifier.weight(1f),
                title = "Current Streak",
                value = "$currentStreak days",
                icon = Icons.Outlined.LocalFireDepartment,
                gradient = listOf(Color(0xFFF59E0B), Color(0xFFEF4444))
            )
            StatCard(
                modifier = Modifier.weight(1f),
                title = "Completed",
                value = completedGoals.size.toString(),
                icon = Icons.Outlined.CheckCircle,
                gradient = listOf(Color(0xFF10B981), Color(0xFF059669))
            )
            StatCard(
                modifier = Modifier.weight(1f),
                title = "Today's Logs",
                value = todayLogs.size.toString(),
                icon = Icons.Outlined.EditNote,
                gradient = listOf(Color(0xFF3B82F6), Color(0xFF0EA5E9))
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Quick Actions
        Text(
            "Quick Actions",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            QuickActionCard(
                modifier = Modifier.weight(1f),
                title = "Add New Goal",
                description = "Set a new target to achieve",
                icon = Icons.Filled.Add,
                onClick = onNavigateToGoals
            )
            QuickActionCard(
                modifier = Modifier.weight(1f),
                title = "Track Habit",
                description = "Mark your daily habits",
                icon = Icons.Filled.CheckCircle,
                onClick = onNavigateToHabits
            )
            QuickActionCard(
                modifier = Modifier.weight(1f),
                title = "Log Progress",
                description = "Update your goal progress",
                icon = Icons.Filled.TrendingUp,
                onClick = onNavigateToGoals
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Active Goals Section
        if (activeGoals.isNotEmpty()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Active Goals",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
                TextButton(onClick = onNavigateToGoals) {
                    Text("View All")
                    Icon(Icons.Filled.ArrowForward, null, Modifier.size(16.dp))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(activeGoals.take(5)) { goal ->
                    GoalProgressCard(goal)
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Habits Section
        if (habits.isNotEmpty()) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Today's Habits",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.SemiBold
                )
                TextButton(onClick = onNavigateToHabits) {
                    Text("View All")
                    Icon(Icons.Filled.ArrowForward, null, Modifier.size(16.dp))
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(habits) { habit ->
                    HabitChip(habit)
                }
            }
        }

        // Empty State
        if (goals.isEmpty() && habits.isEmpty()) {
            Spacer(modifier = Modifier.height(48.dp))
            EmptyDashboard(
                onAddGoal = onNavigateToGoals,
                onAddHabit = onNavigateToHabits
            )
        }
    }
}
@Composable
private fun StatCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    icon: ImageVector,
    gradient: List<Color>
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.linearGradient(gradient))
                .padding(20.dp)
        ) {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.White.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            icon,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    value,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    title,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White.copy(alpha = 0.8f)
                )
            }
        }
    }
}
@Composable
private fun QuickActionCard(
    modifier: Modifier = Modifier,
    title: String,
    description: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )
            Text(
                description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
@Composable
private fun GoalProgressCard(goal: Goal) {
    val progress = if (goal.targetValue > 0) {
        (goal.currentValue.toFloat() / goal.targetValue.toFloat()).coerceIn(0f, 1f)
    } else 0f

    Card(
        modifier = Modifier.width(280.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                AssistChip(
                    onClick = { },
                    label = { Text(goal.category.displayName()) },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                )
                Text(
                    "${(progress * 100).toInt()}%",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                goal.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.SemiBold
            )

            if (goal.description.isNotEmpty()) {
                Text(
                    goal.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 2
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                "${goal.currentValue} / ${goal.targetValue} ${goal.unit}",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
@Composable
private fun HabitChip(habit: Habit) {
    val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    val isCompletedToday = habit.completedDates.any { date ->
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(date)) == today
    }

    FilterChip(
        selected = isCompletedToday,
        onClick = { },
        label = {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(habit.title)
                if (habit.streak > 0) {
                    Spacer(Modifier.width(8.dp))
                    Icon(
                        Icons.Filled.LocalFireDepartment,
                        null,
                        modifier = Modifier.size(14.dp),
                        tint = Color(0xFFF59E0B)
                    )
                    Text(
                        "${habit.streak}",
                        style = MaterialTheme.typography.labelSmall
                    )
                }
            }
        },
        leadingIcon = {
            if (isCompletedToday) {
                Icon(Icons.Filled.CheckCircle, null, Modifier.size(18.dp))
            } else {
                Icon(Icons.Outlined.Circle, null, Modifier.size(18.dp))
            }
        }
    )
}
@Composable
private fun EmptyDashboard(
    onAddGoal: () -> Unit,
    onAddHabit: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(48.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                Icons.Outlined.RocketLaunch,
                contentDescription = null,
                modifier = Modifier.size(80.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                "Start Your Journey!",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                "Create your first goal or habit to begin tracking your progress",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Button(onClick = onAddGoal) {
                    Icon(Icons.Filled.Flag, null, Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Add Goal")
                }
                OutlinedButton(onClick = onAddHabit) {
                    Icon(Icons.Filled.Loop, null, Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Add Habit")
                }
            }
        }
    }
}