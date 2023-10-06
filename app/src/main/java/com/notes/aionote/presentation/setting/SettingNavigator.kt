package com.notes.aionote.presentation.setting

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable

const val settingRoute = "setting_route"

fun NavController.navigateToSetting(navOptions: NavOptions? = null){
	this.navigate(settingRoute, navOptions)
}

fun NavGraphBuilder.settingGraph(
	navigateToSignIn: () -> Unit,
){
	composable(
		route = settingRoute
	){
		SettingRoute(
			navigateToSignIn = navigateToSignIn
		)
	}
}