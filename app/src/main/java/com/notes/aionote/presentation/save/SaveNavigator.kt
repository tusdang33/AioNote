package com.notes.aionote.presentation.save

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable

const val finishedRoute = "finished_route"

fun NavController.navigateToSave(navOptions: NavOptions? = null){
	this.navigate(finishedRoute, navOptions)
}

fun NavGraphBuilder.saveGraph(){
	composable(
		route = finishedRoute
	){
		SaveRoute()
	}
}