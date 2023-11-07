package com.notes.aionote.presentation.setting.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.notes.aionote.presentation.setting.change_password.ChangePasswordRoute

const val changePasswordRoute = "change_password_route"

fun NavController.navigateToChangePassword(
	navOptions: NavOptions? = null,
	email: String,
) {
	this.navigate("$changePasswordRoute?${email}", navOptions)
}

fun NavGraphBuilder.changePasswordGraph(
	onBackClick: () -> Unit,
) {
	composable(
		route = "$changePasswordRoute?{email}",
		arguments = listOf(
			navArgument("email") {
				type = NavType.StringType
			}
		)
	) {
		ChangePasswordRoute(
			onBackClick = onBackClick
		)
	}
}