@file:OptIn(ExperimentalMaterial3Api::class)

package org.example

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
@Composable
fun GoalsScreen(
    goals: List<Goal>,
    onAddGoal: (Goal) -> Unit,
    onUpdateGoal: (Goal) -> Unit,
    onDeleteGoal: (String) -> Unit,
    onLogProgress: (DailyLog) -> Unit
) {
    var showAddDialog by remember { mutableStateOf(false) }
    var showLogDialog by remember { mutableStateOf<Goal?>(null) }
    var selectedCategory by remember { mutableStateOf<GoalCategory?>(null) }

    val filteredGoals = if (selectedCategory != null) {
        goals.filter { it.category == selectedCategory }
    } else goals

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
                    "Goals",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    "${goals.size} total • ${goals.count { it.isCompleted }} completed",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Button(onClick = { showAddDialog = true }) {
                Icon(Icons.Filled.Add, null, Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("New Goal")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Category Filter
        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            FilterChip(
                selected = selectedCategory == null,
                onClick = { selectedCategory = null },
                label = { Text("All") }
            )
            GoalCategory.entries.forEach { category ->
                FilterChip(
                    selected = selectedCategory == category,
                    onClick = {
                        selectedCategory = if (selectedCategory == category) null else category
                    },
                    label = { Text(category.displayName()) }
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Goals List
        if (filteredGoals.isEmpty()) {
            EmptyGoalsState(onAddGoal = { showAddDialog = true })
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(filteredGoals, key = { it.id }) { goal ->
                    GoalCard(
                        goal = goal,
                        onUpdate = onUpdateGoal,
                        onDelete = { onDeleteGoal(goal.id) },
                        onLogProgress = { showLogDialog = goal }
                    )
                }
            }
        }
    }

    // Add Goal Dialog
    if (showAddDialog) {
        AddGoalDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { goal ->
                onAddGoal(goal)
                showAddDialog = false
            }
        )
    }

    // Log Progress Dialog
    showLogDialog?.let { goal ->
        LogProgressDialog(
            goal = goal,
            onDismiss = { showLogDialog = null },
            onConfirm = { value, notes ->
                onLogProgress(DailyLog(
                    goalId = goal.id,
                    value = value,
                    notes = notes
                ))
                showLogDialog = null
            }
        )
    }
}
@Composable
private fun GoalCard(
    goal: Goal,
    onUpdate: (Goal) -> Unit,
    onDelete: () -> Unit,
    onLogProgress: () -> Unit
) {
    val progress = if (goal.targetValue > 0) {
        (goal.currentValue.toFloat() / goal.targetValue.toFloat()).coerceIn(0f, 1f)
    } else 0f

    var expanded by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (goal.isCompleted)
                MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.5f)
            else MaterialTheme.colorScheme.surface
        ),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(modifier = Modifier.padding(20.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Category Badge
                    AssistChip(
                        onClick = { },
                        label = { Text(goal.category.displayName()) },
                        colors = AssistChipDefaults.assistChipColors(
                            containerColor = getCategoryColor(goal.category).copy(alpha = 0.2f),
                            labelColor = getCategoryColor(goal.category)
                        )
                    )

                    if (goal.isCompleted) {
                        Icon(
                            Icons.Filled.CheckCircle,
                            contentDescription = "Completed",
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Box {
                    IconButton(onClick = { expanded = true }) {
                        Icon(Icons.Filled.MoreVert, "More options")
                    }

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Log Progress") },
                            onClick = {
                                expanded = false
                                onLogProgress()
                            },
                            leadingIcon = { Icon(Icons.Outlined.Add, null) }
                        )
                        DropdownMenuItem(
                            text = { Text(if (goal.isCompleted) "Mark Incomplete" else "Mark Complete") },
                            onClick = {
                                expanded = false
                                onUpdate(goal.copy(isCompleted = !goal.isCompleted))
                            },
                            leadingIcon = {
                                Icon(
                                    if (goal.isCompleted) Icons.Outlined.Undo else Icons.Outlined.CheckCircle,
                                    null
                                )
                            }
                        )
                        Divider()
                        DropdownMenuItem(
                            text = { Text("Delete", color = MaterialTheme.colorScheme.error) },
                            onClick = {
                                expanded = false
                                onDelete()
                            },
                            leadingIcon = {
                                Icon(
                                    Icons.Outlined.Delete,
                                    null,
                                    tint = MaterialTheme.colorScheme.error
                                )
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                goal.title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.SemiBold
            )

            if (goal.description.isNotEmpty()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    goal.description,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Progress Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "${goal.currentValue} / ${goal.targetValue} ${goal.unit}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    "${(progress * 100).toInt()}%",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            LinearProgressIndicator(
                progress = progress,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(RoundedCornerShape(5.dp)),
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                color = if (goal.isCompleted) MaterialTheme.colorScheme.secondary
                else MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Action Button
            if (!goal.isCompleted) {
                Button(
                    onClick = onLogProgress,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Filled.Add, null, Modifier.size(18.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Log Progress")
                }
            }
        }
    }
}
@Composable
private fun AddGoalDialog(
    onDismiss: () -> Unit,
    onConfirm: (Goal) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf(GoalCategory.PERSONAL) }
    var targetValue by remember { mutableStateOf("100") }
    var unit by remember { mutableStateOf("%") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create New Goal", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.width(400.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Goal Title") },
                    placeholder = { Text("e.g., Read 20 books") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description (Optional)") },
                    placeholder = { Text("What do you want to achieve?") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    maxLines = 3
                )

                // Category Dropdown
                var categoryExpanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = categoryExpanded,
                    onExpandedChange = { categoryExpanded = it }
                ) {
                    OutlinedTextField(
                        value = category.displayName(),
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("Category") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = categoryExpanded,
                        onDismissRequest = { categoryExpanded = false }
                    ) {
                        GoalCategory.entries.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text(cat.displayName()) },
                                onClick = {
                                    category = cat
                                    categoryExpanded = false
                                }
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    OutlinedTextField(
                        value = targetValue,
                        onValueChange = { targetValue = it.filter { c -> c.isDigit() } },
                        label = { Text("Target") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = unit,
                        onValueChange = { unit = it },
                        label = { Text("Unit") },
                        placeholder = { Text("e.g., books, km, %") },
                        modifier = Modifier.weight(1f),
                        singleLine = true
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    if (title.isNotBlank()) {
                        onConfirm(Goal(
                            title = title,
                            description = description,
                            category = category,
                            targetValue = targetValue.toIntOrNull() ?: 100,
                            unit = unit.ifBlank { "%" }
                        ))
                    }
                },
                enabled = title.isNotBlank()
            ) {
                Text("Create Goal")
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
private fun LogProgressDialog(
    goal: Goal,
    onDismiss: () -> Unit,
    onConfirm: (Int, String) -> Unit
) {
    var value by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }

    val remaining = goal.targetValue - goal.currentValue

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Log Progress", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.width(350.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    "Goal: ${goal.title}",
                    style = MaterialTheme.typography.bodyLarge
                )
                Text(
                    "Remaining: $remaining ${goal.unit}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                OutlinedTextField(
                    value = value,
                    onValueChange = { value = it.filter { c -> c.isDigit() } },
                    label = { Text("Progress Value") },
                    placeholder = { Text("How much did you complete?") },
                    suffix = { Text(goal.unit) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                OutlinedTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = { Text("Notes (Optional)") },
                    placeholder = { Text("Any thoughts or reflections?") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 2,
                    maxLines = 3
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val progressValue = value.toIntOrNull() ?: 0
                    if (progressValue > 0) {
                        onConfirm(progressValue, notes)
                    }
                },
                enabled = value.toIntOrNull()?.let { it > 0 } == true
            ) {
                Text("Log Progress")
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
private fun EmptyGoalsState(onAddGoal: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(48.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            Icons.Outlined.Flag,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f)
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            "No goals yet",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            "Create your first goal to start tracking your progress",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onAddGoal) {
            Icon(Icons.Filled.Add, null, Modifier.size(18.dp))
            Spacer(Modifier.width(8.dp))
            Text("Create Goal")
        }
    }
}
private fun getCategoryColor(category: GoalCategory): Color = when (category) {
    GoalCategory.PERSONAL -> Color(0xFF6366F1)
    GoalCategory.HEALTH -> Color(0xFF10B981)
    GoalCategory.CAREER -> Color(0xFF3B82F6)
    GoalCategory.LEARNING -> Color(0xFFF59E0B)
    GoalCategory.FINANCE -> Color(0xFF059669)
    GoalCategory.FITNESS -> Color(0xFFEF4444)
    GoalCategory.CREATIVE -> Color(0xFF8B5CF6)
}