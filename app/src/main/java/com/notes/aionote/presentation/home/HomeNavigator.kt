package com.notes.aionote.presentation.home

import androidx.navigation.NavArgument
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument

const val homeRoute = "home_route"

fun NavController.navigateToHome(
	navOptions: NavOptions? = null,
	filterId: String = ""
){
	this.navigate("$homeRoute?${filterId}",navOptions)
}

fun NavGraphBuilder.homeGraph(
	navigateToNote: (String) -> Unit,
	navigateToTask: (String) -> Unit,
	onChangeCurrentPage: (Int) -> Unit,
	navigateToCategory: (String?) -> Unit
){
	composable(
		route = "$homeRoute?{filterId}",
		arguments = listOf(
			navArgument("filterId") {
				type = NavType.StringType
			}
		)
	){
		HomeRoute(
			navigateToNote = navigateToNote,
			navigateToTask = navigateToTask,
			onChangeCurrentPage = onChangeCurrentPage,
			navigateToCategory = navigateToCategory
		)
	}
}