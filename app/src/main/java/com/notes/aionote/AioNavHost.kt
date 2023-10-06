package com.notes.aionote

import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.notes.aionote.presentation.authentication.sign_in.navigateToSignIn
import com.notes.aionote.presentation.authentication.sign_in.signInGraph
import com.notes.aionote.presentation.authentication.sign_up.navigateToSignUp
import com.notes.aionote.presentation.authentication.sign_up.signUpGraph
import com.notes.aionote.presentation.home.homeGraph
import com.notes.aionote.presentation.home.navigateToHome
import com.notes.aionote.presentation.note.navigateToNote
import com.notes.aionote.presentation.note.noteGraph
import com.notes.aionote.presentation.save.saveGraph
import com.notes.aionote.presentation.search.searchGraph
import com.notes.aionote.presentation.setting.settingGraph
import com.notes.aionote.presentation.splash.splashGraph
import com.notes.aionote.presentation.splash.splashRoute

@Composable
fun AioNavHost(
	modifier: Modifier = Modifier,
	navController: NavHostController,
	snackbarHostState: SnackbarHostState
) {
	NavHost(
		modifier = modifier,
		navController = navController,
		startDestination = splashRoute
	) {
		splashGraph(
			navigateToHome = { navController.navigateToHome() },
			navigateToSignIn = { navController.navigateToSignIn() }
		)
		homeGraph(
			navigateToNote = {
				navController.navigateToNote(noteId = it)
			}
		)
		searchGraph()
		saveGraph()
		settingGraph(
			navigateToSignIn = { navController.navigateToSignIn() }
		)
		signInGraph(
			snackbarHostState = snackbarHostState,
			navigateToHome = { navController.navigateToHome() },
			navigateToSignUp = { navController.navigateToSignUp() })
		signUpGraph(
			snackbarHostState = snackbarHostState,
			navigateToSignIn = { navController.navigateToSignIn() },
			navigateToHome = { navController.navigateToHome() }
		)
		noteGraph(
			onBackClick = {
				navController.popBackStack()
			}
		)
	}
}