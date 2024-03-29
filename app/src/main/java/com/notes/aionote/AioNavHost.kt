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
import com.notes.aionote.presentation.category.categoryGraph
import com.notes.aionote.presentation.category.navigateToCategory
import com.notes.aionote.presentation.home.homeGraph
import com.notes.aionote.presentation.home.navigateToHome
import com.notes.aionote.presentation.note.navigation.navigateToNote
import com.notes.aionote.presentation.note.navigation.navigateToTask
import com.notes.aionote.presentation.note.navigation.noteGraph
import com.notes.aionote.presentation.note.navigation.taskGraph
import com.notes.aionote.presentation.finished.finishedGraph
import com.notes.aionote.presentation.search.searchGraph
import com.notes.aionote.presentation.setting.navigation.changePasswordGraph
import com.notes.aionote.presentation.setting.navigation.editProfileGraph
import com.notes.aionote.presentation.setting.navigation.navigateToChangePassword
import com.notes.aionote.presentation.setting.navigation.navigateToEditProfile
import com.notes.aionote.presentation.setting.navigation.settingGraph
import com.notes.aionote.presentation.splash.splashGraph
import com.notes.aionote.presentation.splash.splashRoute

@Composable
fun AioNavHost(
	modifier: Modifier = Modifier,
	appState: AioAppState,
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
			},
			navigateToTask = {
				navController.navigateToTask(taskId = it)
			},
			onChangeCurrentPage = {
				appState.changeCurrentPage(it)
			},
			navigateToCategory = {
				navController.navigateToCategory(noteId = it ?: "")
			}
		)
		searchGraph(
			onBackClick = {
				navController.popBackStack()
			},
			navigateToNote = {
				navController.navigateToNote(noteId = it)
			},
			navigateToTask = {
				navController.navigateToTask(taskId = it)
			},
			navigateToCategory = {
				navController.navigateToCategory(noteId = it ?: "")
			}
		)
		finishedGraph(
			onBackClick = {navController.popBackStack()},
			navigateToTask = {
//				navController.navigateToTask(taskId = it)
			}
		)
		settingGraph(
			navigateToSignIn = { navController.navigateToSignIn() },
			navigateToEditProfile = { image, name, email ->
				navController.navigateToEditProfile(
					image = image,
					userEmail = email,
					userName = name
				)
			},
			navigateToChangePassword = {
				navController.navigateToChangePassword(email = it)
			},
			onBackClick = {
				navController.popBackStack()
			},
			nestedGraph = {
				editProfileGraph(
					onBackClick = {
						navController.popBackStack()
					}
				)
				changePasswordGraph(
					onBackClick = {
						navController.popBackStack()
					}
				)
			}
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
		taskGraph(
			onBackClick = {
				navController.popBackStack()
			}
		)
		categoryGraph(
			onBackClick = {
				navController.popBackStack()
			},
			navigateToHomeWithFilter = {
				navController.navigateToHome(filterId = it)
			}
		)
	}
}