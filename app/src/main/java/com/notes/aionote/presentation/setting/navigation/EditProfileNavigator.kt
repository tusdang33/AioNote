package com.notes.aionote.presentation.setting.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.notes.aionote.presentation.setting.edit_profile.EditProfileRoute

const val editProfileRoute = "edit_profile_route"

fun NavController.navigateToEditProfile(
	navOptions: NavOptions? = null,
	image: String?,
	userName: String?,
	userEmail: String
) {
	this.navigate("$editProfileRoute?${image}?${userName}?${userEmail}", navOptions)
}

fun NavGraphBuilder.editProfileGraph(
	onBackClick: () -> Unit,
) {
	composable(
		route = "$editProfileRoute?{image}?{userName}?{userEmail}",
		arguments = listOf(
			navArgument("image") {
				type = NavType.StringType
			},
			navArgument("userName") {
				type = NavType.StringType
			},
			navArgument("userEmail") {
				type = NavType.StringType
			},
		)
	) {
		EditProfileRoute(
			onBackClick = onBackClick
		)
	}
}