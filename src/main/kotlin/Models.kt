package org.example
import kotlinx.serialization.Serializable
@Serializable
data class Goal(
    val id: String = java.util.UUID.randomUUID().toString(),
    val title: String,
    val description: String = "",
    val category: GoalCategory = GoalCategory.PERSONAL,
    val targetValue: Int = 100,
    val currentValue: Int = 0,
    val unit: String = "%",
    val createdAt: Long = System.currentTimeMillis(),
    val deadline: Long? = null,
    val isCompleted: Boolean = false,
    val milestones: List<Milestone> = emptyList()
)
@Serializable
data class Milestone(
    val id: String = java.util.UUID.randomUUID().toString(),
    val title: String,
    val targetValue: Int,
    val isCompleted: Boolean = false,
    val completedAt: Long? = null
)
@Serializable
data class DailyLog(
    val id: String = java.util.UUID.randomUUID().toString(),
    val goalId: String,
    val date: Long = System.currentTimeMillis(),
    val value: Int,
    val notes: String = ""
)
@Serializable
data class Habit(
    val id: String = java.util.UUID.randomUUID().toString(),
    val title: String,
    val description: String = "",
    val frequency: HabitFrequency = HabitFrequency.DAILY,
    val targetDays: Int = 30,
    val completedDates: List<Long> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
    val streak: Int = 0,
    val longestStreak: Int = 0
)
@Serializable
enum class GoalCategory {
    PERSONAL,
    HEALTH,
    CAREER,
    LEARNING,
    FINANCE,
    FITNESS,
    CREATIVE
}
@Serializable
enum class HabitFrequency {
    DAILY,
    WEEKLY,
    WEEKDAYS,
    WEEKENDS
}
fun GoalCategory.displayName(): String = when(this) {
    GoalCategory.PERSONAL -> "Personal"
    GoalCategory.HEALTH -> "Health"
    GoalCategory.CAREER -> "Career"
    GoalCategory.LEARNING -> "Learning"
    GoalCategory.FINANCE -> "Finance"
    GoalCategory.FITNESS -> "Fitness"
    GoalCategory.CREATIVE -> "Creative"
}