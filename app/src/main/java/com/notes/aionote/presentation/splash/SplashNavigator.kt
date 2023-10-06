package com.notes.aionote.presentation.splash

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.compose.composable
import com.notes.aionote.presentation.search.SearchRoute
import com.notes.aionote.presentation.search.searchRoute

const val splashRoute = "splash_route"

fun NavController.navigateToSplash(navOptions: NavOptions? = null){
	this.navigate(splashRoute, navOptions)
}

fun NavGraphBuilder.splashGraph(
	navigateToSignIn: ()->Unit,
	navigateToHome: ()->Unit
){
	composable(
		route = splashRoute
	){
		SplashRoute(
			navigateToHome = navigateToHome,
			navigateToSignIn = navigateToSignIn
		)
	}
}