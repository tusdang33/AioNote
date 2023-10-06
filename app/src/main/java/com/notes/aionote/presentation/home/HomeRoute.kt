package com.notes.aionote.presentation.home

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.notes.aionote.collectInLaunchedEffectWithLifecycle
import com.notes.aionote.presentation.note.components.AioNotePreview
import com.notes.aionote.presentation.note.components.NoteToolbarItem
import com.notes.aionote.ui.theme.AioComposeTheme
import com.notes.aionote.ui.theme.AioTheme

@Composable
fun HomeRoute(
	homeViewModel: HomeViewModel = hiltViewModel(),
	navigateToNote: (String) -> Unit,
) {
	val homeUiState by homeViewModel.uiState.collectAsStateWithLifecycle()
	
	HomeScreen(
		modifier = Modifier
			.fillMaxSize()
			.background(AioTheme.neutralColor.light.copy(alpha = 0.5f)),
		homeUiState = homeUiState,
		onEvent = homeViewModel::onEvent
	)
	
	homeViewModel.oneTimeEvent.collectInLaunchedEffectWithLifecycle { noteOneTimeEvent ->
		when (noteOneTimeEvent) {
			is HomeOneTimeEvent.NavigateToNote -> {
				navigateToNote.invoke(noteOneTimeEvent.noteId)
			}
			
			else -> {/*noop*/}
		}
	}
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
	modifier: Modifier = Modifier,
	homeUiState: HomeUiState,
	onEvent: (HomeEvent) -> Unit,
) {
	LazyVerticalStaggeredGrid(
		modifier = modifier,
		columns = StaggeredGridCells.Fixed(2),
		contentPadding = PaddingValues(12.dp),
		horizontalArrangement = Arrangement.spacedBy(12.dp),
		verticalItemSpacing = 12.dp
	) {
		itemsIndexed(homeUiState.listNote) { index, note ->
			AioNotePreview(
				note = note,
				onNoteClick = { onEvent(HomeEvent.NavigateToEditNote(it)) },
				onToolbarItemClick = { toolbar ->
					when (toolbar) {
						NoteToolbarItem.DELETE -> onEvent(HomeEvent.DeleteItem(index))
					}
				}
			)
		}
	}
}

@Preview
@Composable
private fun PreviewHomeRoute() {
	AioComposeTheme {
		HomeRoute() {}
	}
}