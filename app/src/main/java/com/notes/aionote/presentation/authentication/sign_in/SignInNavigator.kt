package com.notes.aionote.presentation.authentication.sign_in

import androidx.compose.material3.SnackbarHostState
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.notes.aionote.presentation.home.HomeRoute
import com.notes.aionote.presentation.home.homeRoute

const val signInRoute = "sign_in_route"

fun NavController.navigateToSignIn(
	navOptions: NavOptions? = null
){
	this.navigate(signInRoute, navOptions)
}

fun NavGraphBuilder.signInGraph(
	navigateToSignUp: () -> Unit,
	navigateToHome: () -> Unit,
	snackbarHostState: SnackbarHostState,
){
	composable(
		route = signInRoute
	){
		SignInRoute(
			snackbarHostState = snackbarHostState,
			navigateToSignUp = navigateToSignUp,
			navigateToHome = navigateToHome
		)
	}
}