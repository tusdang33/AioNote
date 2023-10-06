package com.notes.aionote.presentation.authentication.sign_up

import androidx.compose.material3.SnackbarHostState
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable

const val signUpRoute = "sign_up_route"

fun NavController.navigateToSignUp(
	navOptions: NavOptions? = null
) {
	this.navigate(signUpRoute, navOptions)
}

fun NavGraphBuilder.signUpGraph(
	navigateToSignIn: () -> Unit,
	navigateToHome: () -> Unit,
	snackbarHostState: SnackbarHostState,
) {
	composable(
		route = signUpRoute
	) {
		SignUpRoute(
			snackbarHostState = snackbarHostState,
			navigateToSignIn = navigateToSignIn,
			navigateToHome = navigateToHome
		)
	}
}