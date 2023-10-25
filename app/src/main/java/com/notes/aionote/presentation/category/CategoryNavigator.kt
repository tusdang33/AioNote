package com.notes.aionote.presentation.category

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

const val categoryRoute = "category_route"

fun NavController.navigateToCategory(
	navOptions: NavOptions? = null,
	noteId: String = ""
) {
	this.navigate("$categoryRoute?${noteId}", navOptions)
}

fun NavGraphBuilder.categoryGraph(
	onBackClick: () -> Unit,
	navigateToHomeWithFilter: (String) -> Unit,
) {
	composable(
		route = "$categoryRoute?{noteId}",
		arguments = listOf(
			navArgument("noteId") {
				type = NavType.StringType
			}
		)
	) {
		CategoryRoute(
			onBackClick = onBackClick,
			navigateToHomeWithFilter = navigateToHomeWithFilter
		)
	}
}