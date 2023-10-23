package com.notes.aionote

import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetState
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navOptions
import com.google.accompanist.navigation.material.BottomSheetNavigator
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.notes.aionote.common.TopLevelDestination
import com.notes.aionote.presentation.authentication.sign_in.signInRoute
import com.notes.aionote.presentation.authentication.sign_up.signUpRoute
import com.notes.aionote.presentation.home.homeRoute
import com.notes.aionote.presentation.home.navigateToHome
import com.notes.aionote.presentation.note.navigation.navigateToNote
import com.notes.aionote.presentation.note.navigation.navigateToTask
import com.notes.aionote.presentation.note.navigation.noteRoute
import com.notes.aionote.presentation.note.navigation.taskRoute
import com.notes.aionote.presentation.save.navigateToSave
import com.notes.aionote.presentation.save.saveRoute
import com.notes.aionote.presentation.search.navigateToSearch
import com.notes.aionote.presentation.search.searchRoute
import com.notes.aionote.presentation.setting.navigateToSetting
import com.notes.aionote.presentation.setting.settingRoute
import com.notes.aionote.presentation.splash.splashRoute

@OptIn(ExperimentalMaterialApi::class, ExperimentalMaterialNavigationApi::class)
@Composable
fun rememberAppState(
	sheetState: ModalBottomSheetState = rememberModalBottomSheetState(
		initialValue = ModalBottomSheetValue.Hidden,
		confirmValueChange = { it != ModalBottomSheetValue.Expanded },
		skipHalfExpanded = true
	),
	bottomSheetNavigator: BottomSheetNavigator = remember { BottomSheetNavigator(sheetState) },
	navController: NavHostController = rememberNavController(bottomSheetNavigator),
): AioAppState {
	return remember {
		AioAppState(
			navController = navController,
			bottomSheetNavigator = bottomSheetNavigator
		)
	}
}

class AioAppState @OptIn(ExperimentalMaterialNavigationApi::class) constructor(
	val navController: NavHostController,
	val bottomSheetNavigator: BottomSheetNavigator,
) {
	val itemNavigation = TopLevelDestination.values().asList()
	
	private var currentPage: Int = 0
	
	val currentDestination: NavDestination?
		@Composable get() = navController
			.currentBackStackEntryAsState().value?.destination
	
	val currentTopLevelDestination: TopLevelDestination?
		@Composable get() = when (currentDestination?.route) {
			homeRoute -> TopLevelDestination.HOME
			saveRoute -> TopLevelDestination.SAVE
			searchRoute -> TopLevelDestination.SEARCH
			settingRoute -> TopLevelDestination.SETTING
			else -> null
		}
	
	val isShowBottomBar: Boolean
		@Composable
		get() = run {
			val blackList = listOf(
				splashRoute,
				signInRoute,
				signUpRoute,
				noteRoute,
				"$noteRoute?{noteId}"
			)
			
			when {
				blackList.contains(currentDestination?.route) -> false
				else -> true
			}
		}
	
	fun navigateToTopLevelDestination(topLevelDestination: TopLevelDestination) {
		val topLevelNavOptions = navOptions {
			popUpTo(navController.graph.findStartDestination().id) {
				saveState = true
			}
			launchSingleTop = true
			restoreState = true
		}
		
		when (topLevelDestination) {
			TopLevelDestination.HOME -> navController.navigateToHome()
			TopLevelDestination.SAVE -> navController.navigateToSave()
			TopLevelDestination.SEARCH -> navController.navigateToSearch()
			TopLevelDestination.SETTING -> navController.navigateToSetting()
		}
	}
	
	fun navigateToNoteScreen() {
		if (currentPage == 0) {
			navController.navigateToNote()
		} else {
			navController.navigateToTask()
		}
	}
	
	fun changeCurrentPage(index: Int) {
		currentPage = index
	}
}

fun NavDestination?.isTopLevelDestinationInHierarchy(destination: TopLevelDestination) =
	this?.hierarchy?.any {
		it.route?.contains(destination.name, true) ?: false
	} ?: false