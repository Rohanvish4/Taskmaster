package com.hacktheweb.taskmaster.model

data class Task(
    val id: Int,
    val title: String,
    val description: String,
    val priority: Priority,
    val isCompleted: Boolean = false,
    val dueDate: String = "" // Format: "YYYY-MM-DD"
)

enum class Priority {
    LOW, MEDIUM, HIGH
}