package org.example

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.*
@Composable
fun AnalyticsScreen(
    goals: List<Goal>,
    habits: List<Habit>,
    dailyLogs: List<DailyLog>
) {
    val scrollState = rememberScrollState()

    // Calculate analytics
    val totalGoals = goals.size
    val completedGoals = goals.count { it.isCompleted }
    val avgProgress = if (goals.isNotEmpty()) {
        goals.map { goal ->
            if (goal.targetValue > 0) (goal.currentValue.toFloat() / goal.targetValue * 100).coerceIn(0f, 100f)
            else 0f
        }.average().toInt()
    } else 0

    val totalHabits = habits.size
    val totalStreak = habits.sumOf { it.streak }
    val bestStreak = habits.maxOfOrNull { it.longestStreak } ?: 0

    // Weekly habit completion
    val last7Days = (0..6).map { daysAgo ->
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, -daysAgo)
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(cal.time)
    }.reversed()

    val weeklyCompletion = last7Days.map { dateStr ->
        val completed = habits.count { habit ->
            habit.completedDates.any { date ->
                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(date)) == dateStr
            }
        }
        dateStr to completed
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(32.dp)
    ) {
        // Header
        Text(
            "Analytics",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )
        Text(
            "Track your progress over time",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Overview Stats
        Text(
            "Overview",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(16.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            AnalyticsCard(
                modifier = Modifier.weight(1f),
                title = "Goals Progress",
                value = "$avgProgress%",
                subtitle = "Average completion",
                icon = Icons.Outlined.TrendingUp,
                color = Color(0xFF6366F1)
            )
            AnalyticsCard(
                modifier = Modifier.weight(1f),
                title = "Goals Completed",
                value = "$completedGoals/$totalGoals",
                subtitle = "Total goals achieved",
                icon = Icons.Outlined.CheckCircle,
                color = Color(0xFF10B981)
            )
            AnalyticsCard(
                modifier = Modifier.weight(1f),
                title = "Current Streak",
                value = "$totalStreak",
                subtitle = "Days across habits",
                icon = Icons.Outlined.LocalFireDepartment,
                color = Color(0xFFF59E0B)
            )
            AnalyticsCard(
                modifier = Modifier.weight(1f),
                title = "Best Streak",
                value = "$bestStreak",
                subtitle = "Longest streak ever",
                icon = Icons.Outlined.EmojiEvents,
                color = Color(0xFF8B5CF6)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Goals by Category
        Text(
            "Goals by Category",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                GoalCategory.entries.forEach { category ->
                    val categoryGoals = goals.filter { it.category == category }
                    if (categoryGoals.isNotEmpty()) {
                        val completed = categoryGoals.count { it.isCompleted }
                        val progress = if (categoryGoals.isNotEmpty()) {
                            categoryGoals.map { goal ->
                                if (goal.targetValue > 0) goal.currentValue.toFloat() / goal.targetValue
                                else 0f
                            }.average().toFloat()
                        } else 0f

                        CategoryProgressRow(
                            category = category,
                            count = categoryGoals.size,
                            completed = completed,
                            progress = progress
                        )

                        if (category != GoalCategory.entries.last()) {
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }

                if (goals.isEmpty()) {
                    Text(
                        "No goals to analyze yet",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Weekly Habit Completion
        Text(
            "Weekly Habit Completion",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                if (habits.isNotEmpty()) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        weeklyCompletion.forEach { (dateStr, completed) ->
                            val dayName = SimpleDateFormat("EEE", Locale.getDefault())
                                .format(SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(dateStr))
                            val isToday = dateStr == SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())

                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    dayName,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (isToday) MaterialTheme.colorScheme.primary
                                    else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                                Spacer(modifier = Modifier.height(8.dp))

                                val progress = if (totalHabits > 0) completed.toFloat() / totalHabits else 0f
                                Box(
                                    modifier = Modifier
                                        .size(48.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(
                                            when {
                                                progress >= 1f -> Color(0xFF10B981)
                                                progress >= 0.5f -> Color(0xFFF59E0B)
                                                progress > 0f -> Color(0xFFFEF3C7)
                                                else -> MaterialTheme.colorScheme.surfaceVariant
                                            }
                                        ),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        "$completed",
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = if (progress > 0) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }

                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    dateStr.takeLast(2),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                } else {
                    Text(
                        "No habits to track yet",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Recent Activity
        Text(
            "Recent Activity",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )

        Spacer(modifier = Modifier.height(16.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                val recentLogs = dailyLogs.sortedByDescending { it.date }.take(10)

                if (recentLogs.isNotEmpty()) {
                    recentLogs.forEachIndexed { index, log ->
                        val goal = goals.find { it.id == log.goalId }
                        if (goal != null) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Filled.Add,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        "Added ${log.value} ${goal.unit} to ${goal.title}",
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                    Text(
                                        SimpleDateFormat("MMM d, yyyy HH:mm", Locale.getDefault())
                                            .format(Date(log.date)),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            if (index < recentLogs.lastIndex) {
                                Spacer(modifier = Modifier.height(16.dp))
                                Divider(color = MaterialTheme.colorScheme.outlineVariant)
                                Spacer(modifier = Modifier.height(16.dp))
                            }
                        }
                    }
                } else {
                    Text(
                        "No activity logged yet",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}
@Composable
private fun AnalyticsCard(
    modifier: Modifier = Modifier,
    title: String,
    value: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    color: Color
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(color.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    icon,
                    contentDescription = null,
                    tint = color,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium
            )
            Text(
                subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
@Composable
private fun CategoryProgressRow(
    category: GoalCategory,
    count: Int,
    completed: Int,
    progress: Float
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    category.displayName(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                AssistChip(
                    onClick = { },
                    label = { Text("$count goals") },
                    modifier = Modifier.height(24.dp)
                )
            }
            Text(
                "$completed completed",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier
                    .width(120.dp)
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp)),
                trackColor = MaterialTheme.colorScheme.surfaceVariant
            )
            Text(
                "${(progress * 100).toInt()}%",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}