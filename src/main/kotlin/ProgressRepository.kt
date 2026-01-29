package org.example

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
class ProgressRepository {
    private val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    }

    private val dataDir = File(System.getProperty("user.home"), ".progresstracker")
    private val goalsFile = File(dataDir, "goals.json")
    private val habitsFile = File(dataDir, "habits.json")
    private val logsFile = File(dataDir, "daily_logs.json")

    init {
        if (!dataDir.exists()) {
            dataDir.mkdirs()
        }
    }

    // Goals
    fun saveGoals(goals: List<Goal>) {
        goalsFile.writeText(json.encodeToString(goals))
    }

    fun loadGoals(): List<Goal> {
        return try {
            if (goalsFile.exists()) {
                json.decodeFromString<List<Goal>>(goalsFile.readText())
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    // Habits
    fun saveHabits(habits: List<Habit>) {
        habitsFile.writeText(json.encodeToString(habits))
    }

    fun loadHabits(): List<Habit> {
        return try {
            if (habitsFile.exists()) {
                json.decodeFromString<List<Habit>>(habitsFile.readText())
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }

    // Daily Logs
    fun saveDailyLogs(logs: List<DailyLog>) {
        logsFile.writeText(json.encodeToString(logs))
    }

    fun loadDailyLogs(): List<DailyLog> {
        return try {
            if (logsFile.exists()) {
                json.decodeFromString<List<DailyLog>>(logsFile.readText())
            } else {
                emptyList()
            }
        } catch (e: Exception) {
            emptyList()
        }
    }
}