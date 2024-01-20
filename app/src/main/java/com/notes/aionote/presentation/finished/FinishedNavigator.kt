package com.notes.aionote.presentation.finished

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable

const val finishedRoute = "finished_route"

fun NavController.navigateToFinished(navOptions: NavOptions? = null) {
	this.navigate(finishedRoute, navOptions)
}

fun NavGraphBuilder.finishedGraph(
	onBackClick: () -> Unit,
	navigateToTask: (String) -> Unit,
) {
	composable(
		route = finishedRoute
	) {
		FinishedRoute(
			onBackClick = onBackClick,
			navigateToTask = navigateToTask
		)
	}
}