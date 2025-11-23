package com.hacktheweb.taskmaster.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.hacktheweb.taskmaster.model.Priority
import com.hacktheweb.taskmaster.model.Task
import com.hacktheweb.taskmaster.viewmodel.SortOption
import com.hacktheweb.taskmaster.viewmodel.TaskViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskListScreen(
    viewModel: TaskViewModel,
    onNavigateToAdd: () -> Unit,
    onNavigateToDetail: (Int) -> Unit
) {
    var showCompletedTasks by remember { mutableStateOf(true) }
    var selectedPriority by remember { mutableStateOf<Priority?>(null) }
    var sortOption by remember { mutableStateOf(SortOption.PRIORITY) }
    var showStatistics by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("TaskMaster") },
                actions = {
                    TextButton(onClick = { showStatistics = !showStatistics }) {
                        Text("Stats")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = onNavigateToAdd) {
                Icon(Icons.Default.Add, contentDescription = "Add Task")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Statistics Card
            if (showStatistics) {
                val stats = viewModel.getTaskStatistics()
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Total Tasks: ${stats.totalTasks}", style = MaterialTheme.typography.bodyLarge)
                        Text("Completed: ${stats.completedTasks}", style = MaterialTheme.typography.bodyMedium)
                        Text("High Priority Pending: ${stats.pendingHighPriority}", style = MaterialTheme.typography.bodyMedium)
                        Text("Completion Rate: ${stats.completionRate}%", style = MaterialTheme.typography.bodyMedium)
                        Text("Overdue: ${stats.overdueTasks}", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }

            // Filters
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row {
                    FilterChip(
                        selected = showCompletedTasks,
                        onClick = { showCompletedTasks = !showCompletedTasks },
                        label = { Text("Show Completed") }
                    )
                }
            }

            // Task List
            val filteredTasks = viewModel.getFilteredTasks(showCompletedTasks, selectedPriority)
            val sortedTasks = viewModel.getSortedTasks(sortOption)

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(sortedTasks) { task ->
                    TaskItem(
                        task = task,
                        onToggleComplete = { viewModel.toggleTaskCompletion(task.id) },
                        onDelete = { viewModel.deleteTask(task.id) },
                        onClick = { onNavigateToDetail(task.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun TaskItem(
    task: Task,
    onToggleComplete: () -> Unit,
    onDelete: () -> Unit,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = task.isCompleted,
                onCheckedChange = { onToggleComplete() }
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleMedium
                )
                Text(
                    text = task.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.Gray
                )
                Text(
                    text = "Due: ${task.dueDate} | ${task.priority}",
                    style = MaterialTheme.typography.bodySmall,
                    color = when (task.priority) {
                        Priority.HIGH -> Color.Red
                        Priority.MEDIUM -> Color(0xFFFFA500)
                        Priority.LOW -> Color.Green
                    }
                )
            }
            
            IconButton(onClick = onDelete) {
                Icon(Icons.Default.Delete, contentDescription = "Delete")
            }
        }
    }
}