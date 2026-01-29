package org.example

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
enum class Screen {
    DASHBOARD,
    GOALS,
    HABITS,
    ANALYTICS,
    SETTINGS
}
data class NavItem(
    val screen: Screen,
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)
@Composable
fun ProgressTrackerApp() {
    var currentScreen by remember { mutableStateOf(Screen.DASHBOARD) }
    val repository = remember { ProgressRepository() }

    // State
    var goals by remember { mutableStateOf(repository.loadGoals()) }
    var habits by remember { mutableStateOf(repository.loadHabits()) }
    var dailyLogs by remember { mutableStateOf(repository.loadDailyLogs()) }

    val navItems = listOf(
        NavItem(Screen.DASHBOARD, "Dashboard", Icons.Filled.Dashboard, Icons.Outlined.Dashboard),
        NavItem(Screen.GOALS, "Goals", Icons.Filled.Flag, Icons.Outlined.Flag),
        NavItem(Screen.HABITS, "Habits", Icons.Filled.Loop, Icons.Outlined.Loop),
        NavItem(Screen.ANALYTICS, "Analytics", Icons.Filled.BarChart, Icons.Outlined.BarChart),
        NavItem(Screen.SETTINGS, "Settings", Icons.Filled.Settings, Icons.Outlined.Settings)
    )

    Row(modifier = Modifier.fillMaxSize()) {
        // Sidebar Navigation
        NavigationRail(
            modifier = Modifier
                .fillMaxHeight()
                .width(80.dp)
                .background(MaterialTheme.colorScheme.surface),
            containerColor = MaterialTheme.colorScheme.surface,
            header = {
                Box(
                    modifier = Modifier
                        .padding(vertical = 24.dp)
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.primary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Filled.TrendingUp,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        ) {
            navItems.forEach { item ->
                NavigationRailItem(
                    selected = currentScreen == item.screen,
                    onClick = { currentScreen = item.screen },
                    icon = {
                        Icon(
                            if (currentScreen == item.screen) item.selectedIcon else item.unselectedIcon,
                            contentDescription = item.title
                        )
                    },
                    label = { Text(item.title) },
                    colors = NavigationRailItemDefaults.colors(
                        selectedIconColor = MaterialTheme.colorScheme.primary,
                        selectedTextColor = MaterialTheme.colorScheme.primary,
                        indicatorColor = MaterialTheme.colorScheme.primaryContainer
                    )
                )
            }
        }

        // Divider
        Divider(
            modifier = Modifier
                .fillMaxHeight()
                .width(1.dp),
            color = MaterialTheme.colorScheme.outlineVariant
        )

        // Main Content
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
        ) {
            when (currentScreen) {
                Screen.DASHBOARD -> DashboardScreen(
                    goals = goals,
                    habits = habits,
                    dailyLogs = dailyLogs,
                    onNavigateToGoals = { currentScreen = Screen.GOALS },
                    onNavigateToHabits = { currentScreen = Screen.HABITS }
                )
                Screen.GOALS -> GoalsScreen(
                    goals = goals,
                    onAddGoal = { goal ->
                        goals = goals + goal
                        repository.saveGoals(goals)
                    },
                    onUpdateGoal = { updatedGoal ->
                        goals = goals.map { if (it.id == updatedGoal.id) updatedGoal else it }
                        repository.saveGoals(goals)
                    },
                    onDeleteGoal = { goalId ->
                        goals = goals.filter { it.id != goalId }
                        repository.saveGoals(goals)
                    },
                    onLogProgress = { log ->
                        dailyLogs = dailyLogs + log
                        repository.saveDailyLogs(dailyLogs)
                        // Update goal's current value
                        goals = goals.map { goal ->
                            if (goal.id == log.goalId) {
                                goal.copy(currentValue = minOf(goal.currentValue + log.value, goal.targetValue))
                            } else goal
                        }
                        repository.saveGoals(goals)
                    }
                )
                Screen.HABITS -> HabitsScreen(
                    habits = habits,
                    onAddHabit = { habit ->
                        habits = habits + habit
                        repository.saveHabits(habits)
                    },
                    onUpdateHabit = { updatedHabit ->
                        habits = habits.map { if (it.id == updatedHabit.id) updatedHabit else it }
                        repository.saveHabits(habits)
                    },
                    onDeleteHabit = { habitId ->
                        habits = habits.filter { it.id != habitId }
                        repository.saveHabits(habits)
                    }
                )
                Screen.ANALYTICS -> AnalyticsScreen(
                    goals = goals,
                    habits = habits,
                    dailyLogs = dailyLogs
                )
                Screen.SETTINGS -> SettingsScreen()
            }
        }
    }
}