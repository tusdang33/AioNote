package com.notes.aionote.presentation.setting.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.notes.aionote.presentation.setting.SettingRoute

const val settingRoute = "setting_route"
const val settingGraph = "setting_graph"

fun NavController.navigateToSetting(navOptions: NavOptions? = null){
	this.navigate(settingGraph, navOptions)
}

fun NavGraphBuilder.settingGraph(
	navigateToSignIn: () -> Unit,
	navigateToEditProfile: (image: String?, name: String?, email: String) -> Unit,
	navigateToChangePassword: (email: String) -> Unit,
	onBackClick: () -> Unit,
	nestedGraph: () -> Unit
) {
	navigation(route = settingGraph, startDestination = settingRoute) {
		composable(
			route = settingRoute
		) {
			SettingRoute(
				navigateToSignIn = navigateToSignIn,
				navigateToEditProfile = navigateToEditProfile,
				navigateToChangePassword = navigateToChangePassword,
				onBackClick = onBackClick
			)
		}
		nestedGraph()
	}
}