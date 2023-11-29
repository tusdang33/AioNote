package com.notes.aionote

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.work.WorkManager
import com.google.accompanist.navigation.material.ExperimentalMaterialNavigationApi
import com.google.accompanist.navigation.material.ModalBottomSheetLayout
import com.notes.aionote.common.AioConst
import com.notes.aionote.ui.component.AioBottomNavigationBar
import com.notes.aionote.ui.theme.AioComposeTheme
import com.notes.aionote.ui.theme.AioTheme

@OptIn(
	ExperimentalLayoutApi::class,
	ExperimentalMaterialNavigationApi::class,
	ExperimentalMaterialApi::class
)
@Composable
fun AioApp(
	appState: AioAppState = rememberAppState(),
	snackbarHostState: SnackbarHostState = remember { SnackbarHostState() }
) {
	AioComposeTheme {
		ModalBottomSheetLayout(
			sheetShape = RoundedCornerShape(topStart = 10.dp, topEnd = 10.dp),
			sheetBackgroundColor = Color.Transparent,
			sheetContentColor = Color.Transparent,
			bottomSheetNavigator = appState.bottomSheetNavigator,
			sheetElevation = 0.dp
		) {
			Scaffold(modifier = Modifier.fillMaxSize(),
			         snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
			         bottomBar = {
				         CompositionLocalProvider(
					         LocalTextStyle provides AioTheme.regularTypography.base,
					         LocalContentColor provides AioTheme.neutralColor.white
				         ) {
					         if (appState.isShowBottomBar) {
						         AioBottomNavigationBar(
							         modifier = Modifier
								         .fillMaxWidth()
								         .height(80.dp)
								         .background(AioTheme.neutralColor.white),
							         destinations = appState.itemNavigation,
							         currentDestination = appState.currentDestination,
							         onNavigateToDestination = {
								         appState.navigateToTopLevelDestination(
									         it
								         )
							         },
							         isShowFloatingButton = appState.isShowFloatingButton,
							         onFloatingButtonClick = {
								         appState.navigateToNoteScreen()
							         })
					         }
				         }
			         }) { contentPadding ->
				AioNavHost(
					modifier = Modifier
						.padding(contentPadding)
						.consumeWindowInsets(contentPadding),
					appState = appState,
					navController = appState.navController,
					snackbarHostState = snackbarHostState
				)
			}
		}
		
	}
}