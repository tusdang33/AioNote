package com.notes.aionote.presentation.note.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.navArgument
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.bottomSheet
import com.notes.aionote.presentation.note.task.TaskRoute

const val taskRoute = "task_route"

fun NavController.navigateToTask(
	navOptions: NavOptions? = null,
	taskId: String? = null
) {
	this.navigate("$taskRoute?${taskId}", navOptions)
}

@OptIn(ExperimentalMaterialNavigationApi::class)
fun NavGraphBuilder.taskGraph(
	onBackClick: () -> Unit,
) {
	bottomSheet(
		route = "$taskRoute?{taskId}",
		arguments = listOf(
			navArgument("taskId") {
				type = NavType.StringType
			},
		)
	) {
		TaskRoute(
			onBackClick = onBackClick
		)
	}
}