package com.hacktheweb.taskmaster.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import com.hacktheweb.taskmaster.model.Priority
import com.hacktheweb.taskmaster.model.Task
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException
import kotlin.math.roundToInt

class TaskViewModel : ViewModel() {
    private val _tasks = mutableStateListOf<Task>()
    val tasks: List<Task> = _tasks

    private var nextId = 1

    private val isoFormatter: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE

    init {
        
        addTask("Complete project documentation", "Write comprehensive docs", Priority.HIGH, "2024-12-30")
        addTask("Review pull requests", "Check and approve pending PRs", Priority.MEDIUM, "2024-12-25")
        addTask("Fix navigation bug", "Users report navigation issues", Priority.HIGH, "2024-12-20")
    }

    
    fun addTask(title: String, description: String, priority: Priority, dueDate: String) {
        val newTask = Task(
            id = nextId++,
            title = title,
            description = description,
            priority = priority,
            dueDate = dueDate
        )
        _tasks.add(newTask)
    }

    
    fun toggleTaskCompletion(taskId: Int) {
        val index = _tasks.indexOfFirst { it.id == taskId }
        if (index != -1) {
            val task = _tasks[index]
            _tasks[index] = task.copy(isCompleted = !task.isCompleted)
        }
    }

    fun deleteTask(taskId: Int) {
        _tasks.removeAll { it.id == taskId }
    }

    fun getTaskById(taskId: Int): Task? {
        return _tasks.find { it.id == taskId }
    }

    
    
    
    fun getFilteredTasks(showCompleted: Boolean, filterPriority: Priority?): List<Task> {
        return _tasks.filter { task ->
            val completionMatch = showCompleted || !task.isCompleted
            val priorityMatch = filterPriority?.let { task.priority == it } ?: true
            completionMatch && priorityMatch
        }
    }

    
    fun getSortedTasks(sortBy: SortOption): List<Task> {
        return when (sortBy) {
            SortOption.PRIORITY -> {
                
                _tasks.sortedWith(compareByDescending { it.priority.ordinal })
            }
            SortOption.DUE_DATE -> {
                _tasks.sortedWith(compareBy { parseDateOrNull(it.dueDate) ?: LocalDate.MAX })
            }
            SortOption.TITLE -> _tasks.sortedBy { it.title.lowercase() }
            SortOption.COMPLETION -> {
                
                _tasks.sortedBy { it.isCompleted }
            }
        }
    }

    
    fun updateTask(taskId: Int, title: String, description: String, priority: Priority, dueDate: String) {
        val index = _tasks.indexOfFirst { it.id == taskId }
        if (index != -1) {
            val existing = _tasks[index]
            _tasks[index] = existing.copy(
                title = title,
                description = description,
                priority = priority,
                dueDate = dueDate
            )
        }
    }

    
    fun getTaskStatistics(): TaskStatistics {
        val total = _tasks.size
        val completed = _tasks.count { it.isCompleted }
        val highPriority = _tasks.count { it.priority == Priority.HIGH && !it.isCompleted }

        val completionRate = if (total > 0) {
            ((completed.toDouble() / total) * 100).roundToInt()
        } else {
            0
        }

        val now = getCurrentLocalDate()
        val overdue = _tasks.count { task ->
            if (task.isCompleted) {
                false
            } else {
                val due = parseDateOrNull(task.dueDate)
                due != null && due.isBefore(now)
            }
        }

        return TaskStatistics(
            totalTasks = total,
            completedTasks = completed,
            pendingHighPriority = highPriority,
            completionRate = completionRate,
            overdueTasks = overdue
        )
    }

    
    private fun getCurrentLocalDate(): LocalDate {
        return LocalDate.now()
    }

    
    private fun getCurrentDate(): String {
        return getCurrentLocalDate().format(isoFormatter)
    }

    
    private fun parseDateOrNull(dateStr: String?): LocalDate? {
        if (dateStr.isNullOrBlank()) return null
        return try {
            LocalDate.parse(dateStr, isoFormatter)
        } catch (e: DateTimeParseException) {
            null
        }
    }
}

enum class SortOption {
    PRIORITY, DUE_DATE, TITLE, COMPLETION
}

data class TaskStatistics(
    val totalTasks: Int,
    val completedTasks: Int,
    val pendingHighPriority: Int,
    val completionRate: Int,
    val overdueTasks: Int
)
