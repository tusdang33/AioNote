package com.notes.aionote.presentation.save

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.notes.aionote.presentation.search.SearchRoute
import com.notes.aionote.presentation.search.searchRoute

const val saveRoute = "save_route"

fun NavController.navigateToSave(navOptions: NavOptions? = null){
	this.navigate(saveRoute, navOptions)
}

fun NavGraphBuilder.saveGraph(){
	composable(
		route = saveRoute
	){
		SaveRoute()
	}
}