@file:OptIn(ExperimentalMaterial3Api::class)

package org.example

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitsScreen(
    habits: List<Habit>,
    onAddHabit: (Habit) -> Unit,
    onUpdateHabit: (Habit) -> Unit,
    onDeleteHabit: (String) -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
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
                    "Habits",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "${habits.size} habits tracked",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Button(onClick = { showAddDialog = true }) {
                Icon(Icons.Filled.Add, null, Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("New Habit")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Today's Summary Card
        TodaySummaryCard(habits = habits, onToggleHabit = { habit ->
            val today = System.currentTimeMillis()
            val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(today))
            val isCompletedToday = habit.completedDates.any { date ->
                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(date)) == todayStr
            }

            if (isCompletedToday) {
                // Remove today from completed dates
                onUpdateHabit(habit.copy(
                    completedDates = habit.completedDates.filter { date ->
                        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(date)) != todayStr
                    },
                    streak = maxOf(0, habit.streak - 1)
                ))
            } else {
                // Add today to completed dates
                val newStreak = habit.streak + 1
                onUpdateHabit(habit.copy(
                    completedDates = habit.completedDates + today,
                    streak = newStreak,
                    longestStreak = maxOf(habit.longestStreak, newStreak)
                ))
            }
        })

        Spacer(modifier = Modifier.height(24.dp))

        // Habits List
        if (habits.isEmpty()) {
            EmptyHabitsState(onAddHabit = { showAddDialog = true })
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(habits, key = { it.id }) { habit ->
                    HabitCard(
                        habit = habit,
                        onToggle = {
                            val today = System.currentTimeMillis()
                            val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(today))
                            val isCompletedToday = habit.completedDates.any { date ->
                                SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(date)) == todayStr
                            }

                            if (isCompletedToday) {
                                onUpdateHabit(habit.copy(
                                    completedDates = habit.completedDates.filter { date ->
                                        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(date)) != todayStr
                                    },
                                    streak = maxOf(0, habit.streak - 1)
                                ))
                            } else {
                                val newStreak = habit.streak + 1
                                onUpdateHabit(habit.copy(
                                    completedDates = habit.completedDates + today,
                                    streak = newStreak,
                                    longestStreak = maxOf(habit.longestStreak, newStreak)
                                ))
                            }
                        },
                        onDelete = { onDeleteHabit(habit.id) }
                    )
                }
            }
        }
    }

    // Add Habit Dialog
    if (showAddDialog) {
        AddHabitDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { habit ->
                onAddHabit(habit)
                showAddDialog = false
            }
        )
    }
}

@Composable
private fun TodaySummaryCard(
    habits: List<Habit>,
    onToggleHabit: (Habit) -> Unit
) {
    val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    val completedToday = habits.count { habit ->
        habit.completedDates.any { date ->
            SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(date)) == today
        }
    }
    val progress = if (habits.isNotEmpty()) completedToday.toFloat() / habits.size else 0f

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
    ) {
        Column(modifier = Modifier.padding(24.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        "Today's Progress",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Text(
                        "$completedToday of ${habits.size} habits completed",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                }

                // Circular Progress
                Box(contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(
                        progress = progress,
                        modifier = Modifier.size(80.dp),
                        strokeWidth = 8.dp,
                        trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                        color = MaterialTheme.colorScheme.primary
                    )
                    Text(
                        "${(progress * 100).toInt()}%",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            if (habits.isNotEmpty()) {
                Spacer(modifier = Modifier.height(16.dp))
                Divider(color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.2f))
                Spacer(modifier = Modifier.height(16.dp))

                // Quick Toggle for Habits
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    habits.take(3).forEach { habit ->
                        HabitQuickToggle(habit = habit, onToggle = { onToggleHabit(habit) })
                    }
                }
            }
        }
    }
}

@Composable
private fun HabitQuickToggle(
    habit: Habit,
    onToggle: () -> Unit
) {
    val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    val isCompletedToday = habit.completedDates.any { date ->
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(date)) == today
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.5f))
            .clickable(onClick = onToggle)
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            habit.title,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )

        Checkbox(
            checked = isCompletedToday,
            onCheckedChange = { onToggle() }
        )
    }
}

@Composable
private fun HabitCard(
    habit: Habit,
    onToggle: () -> Unit,
    onDelete: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
    val isCompletedToday = habit.completedDates.any { date ->
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(date)) == today
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isCompletedToday)
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
            else MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(
            1.dp,
            if (isCompletedToday) MaterialTheme.colorScheme.primary
            else MaterialTheme.colorScheme.outlineVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Checkbox
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(
                        if (isCompletedToday) MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.surfaceVariant
                    )
                    .clickable(onClick = onToggle),
                contentAlignment = Alignment.Center
            ) {
                if (isCompletedToday) {
                    Icon(
                        Icons.Filled.Check,
                        null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Content
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    habit.title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
                if (habit.description.isNotBlank()) {
                    Text(
                        habit.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Streak
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Filled.LocalFireDepartment,
                            null,
                            modifier = Modifier.size(16.dp),
                            tint = if (habit.streak > 0) Color(0xFFF59E0B)
                            else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            "${habit.streak} day streak",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Best Streak
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Outlined.EmojiEvents,
                            null,
                            modifier = Modifier.size(16.dp),
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            "Best: ${habit.longestStreak}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Frequency
                    AssistChip(
                        onClick = { },
                        label = {
                            Text(
                                habit.frequency.name.lowercase().replaceFirstChar { it.uppercase() },
                                style = MaterialTheme.typography.labelSmall
                            )
                        },
                        modifier = Modifier.height(24.dp)
                    )
                }
            }

            // Menu
            Box {
                IconButton(onClick = { expanded = true }) {
                    Icon(Icons.Filled.MoreVert, "Options")
                }

                DropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Delete", color = MaterialTheme.colorScheme.error) },
                        onClick = {
                            expanded = false
                            onDelete()
                        },
                        leadingIcon = {
                            Icon(Icons.Outlined.Delete, null, tint = MaterialTheme.colorScheme.error)
                        }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddHabitDialog(
    onDismiss: () -> Unit,
    onConfirm: (Habit) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var frequency by remember { mutableStateOf(HabitFrequency.DAILY) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create New Habit", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.width(400.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Habit Title") },
                    placeholder = { Text("e.g., Morning meditation") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description (Optional)") },
                    placeholder = { Text("What is this habit about?") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    maxLines = 3
                )

                // Frequency Selection
                Text(
                    "Frequency",
                    style = MaterialTheme.typography.labelLarge
                )
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.horizontalScroll(rememberScrollState())
                ) {
                    HabitFrequency.entries.forEach { freq ->
                        FilterChip(
                            selected = frequency == freq,
                            onClick = { frequency = freq },
                            label = {
                                Text(freq.name.lowercase().replaceFirstChar { it.uppercase() })
                            }
                        )
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onConfirm(
                            Habit(
                                title = title,
                                description = description,
                                frequency = frequency
                            )
                        )
                    }
                },
                enabled = title.isNotBlank()
            ) {
                Text("Create Habit")
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
private fun EmptyHabitsState(onAddHabit: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            Icons.Outlined.Loop,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            "No habits yet",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Build consistency by tracking your daily habits",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onAddHabit) {
            Icon(Icons.Filled.Add, null, Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text("Create Habit")
        }
    }
}