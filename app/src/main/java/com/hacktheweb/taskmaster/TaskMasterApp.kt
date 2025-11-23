package com.hacktheweb.taskmaster

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.hacktheweb.taskmaster.ui.screens.AddTaskScreen
import com.hacktheweb.taskmaster.ui.screens.TaskDetailScreen
import com.hacktheweb.taskmaster.ui.screens.TaskListScreen
import com.hacktheweb.taskmaster.viewmodel.TaskViewModel

@Composable
fun TaskMasterApp() {
    val navController = rememberNavController()
    val viewModel: TaskViewModel = viewModel()

    NavHost(navController = navController, startDestination = "task_list") {
        composable("task_list") {
            TaskListScreen(
                viewModel = viewModel,
                onNavigateToAdd = { navController.navigate("add_task") },
                onNavigateToDetail = { taskId ->
                    navController.navigate("task_detail/$taskId")
                }
            )
        }
        composable("add_task") {
            AddTaskScreen(
                viewModel = viewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable("task_detail/{taskId}") { backStackEntry ->
            val taskId = backStackEntry.arguments?.getString("taskId")?.toIntOrNull()
            if (taskId != null) {
                TaskDetailScreen(
                    viewModel = viewModel,
                    taskId = taskId,
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}