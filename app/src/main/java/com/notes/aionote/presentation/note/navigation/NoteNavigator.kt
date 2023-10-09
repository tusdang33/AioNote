package com.notes.aionote.presentation.note.navigation

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavOptions
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.notes.aionote.presentation.note.normal_note.NoteRoute

const val noteRoute = "note_route"

fun NavController.navigateToNote(
	navOptions: NavOptions? = null,
	noteId: String? = null
){
	this.navigate("$noteRoute?${noteId}", navOptions)
}

fun NavGraphBuilder.noteGraph(
	onBackClick: () -> Unit,
){
	composable(
		route = "$noteRoute?{noteId}",
		arguments = listOf(
			navArgument("noteId") {
				type = NavType.StringType
			},
		)
	){
		NoteRoute(
			onBackClick = onBackClick
		)
	}
}