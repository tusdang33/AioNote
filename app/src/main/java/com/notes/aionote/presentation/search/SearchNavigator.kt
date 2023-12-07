package com.notes.aionote.presentation.search

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable

const val searchRoute = "search_route"

fun NavController.navigateToSearch(navOptions: NavOptions? = null){
	this.navigate(searchRoute, navOptions)
}

fun NavGraphBuilder.searchGraph(
	onBackClick: () -> Unit,
	navigateToNote: (String) -> Unit,
	navigateToTask: (String) -> Unit,
	navigateToCategory: (String?) -> Unit,
){
	composable(
		route = searchRoute
	){
		SearchRoute(
			onBackClick = onBackClick,
			navigateToTask = navigateToTask,
			navigateToCategory = navigateToCategory,
			navigateToNote = navigateToNote
		)
	}
}