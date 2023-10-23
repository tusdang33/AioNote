package com.notes.aionote.presentation.home

import android.content.Context
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.notes.aionote.collectInLaunchedEffectWithLifecycle
import com.notes.aionote.presentation.note.components.AioNoteFilter
import com.notes.aionote.presentation.note.components.AioNotePicker
import com.notes.aionote.presentation.note.components.AioNotePreview
import com.notes.aionote.presentation.note.components.AioTaskPreview
import com.notes.aionote.presentation.note.components.NoteOption
import com.notes.aionote.presentation.note.components.NoteSegment
import com.notes.aionote.presentation.note.components.NoteToolbarItem
import com.notes.aionote.presentation.note.normal_note.NoteEvent
import com.notes.aionote.ui.theme.AioComposeTheme
import com.notes.aionote.ui.theme.AioTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun HomeRoute(
	homeViewModel: HomeViewModel = hiltViewModel(),
	onChangeCurrentPage: (Int) -> Unit,
	navigateToNote: (String) -> Unit,
	navigateToTask: (String) -> Unit,
) {
	val homeUiState by homeViewModel.uiState.collectAsStateWithLifecycle()
	
	HomeScreen(
		modifier = Modifier
			.fillMaxSize()
			.background(AioTheme.neutralColor.light),
		homeUiState = homeUiState,
		onEvent = homeViewModel::onEvent
	)
	
	homeViewModel.oneTimeEvent.collectInLaunchedEffectWithLifecycle { noteOneTimeEvent ->
		when (noteOneTimeEvent) {
			is HomeOneTimeEvent.NavigateToNote -> {
				navigateToNote.invoke(noteOneTimeEvent.noteId)
			}
			
			is HomeOneTimeEvent.NavigateToTask -> {
				navigateToTask.invoke(noteOneTimeEvent.noteId)
			}
			
			is HomeOneTimeEvent.ChangeCurrentPage -> {
				onChangeCurrentPage.invoke(noteOneTimeEvent.page)
			}
			
			else -> { /*noop*/ }
		}
	}
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
	modifier: Modifier = Modifier,
	homeUiState: HomeUiState,
	onEvent: (HomeEvent) -> Unit,
	context: Context = LocalContext.current,
	coroutineScope: CoroutineScope = rememberCoroutineScope()
) {
	val pageState = rememberPagerState()
	
	var holdingNotePicker: NoteSegment? by remember {
		mutableStateOf(NoteSegment.LIST)
	}
	
	LaunchedEffect(pageState.currentPage) {
		holdingNotePicker = if(pageState.currentPage == 0 ) {
			NoteSegment.LIST
		} else {
			NoteSegment.CHECK
		}
		onEvent(HomeEvent.ChangePage(pageState.currentPage))
	}
	
	
	Column(modifier = modifier) {
		AioNotePicker(
			modifier = Modifier.padding(vertical = 5.dp),
			pickers = NoteSegment.values().toList(),
			holdingNotePicker = holdingNotePicker,
			backgroundColor = AioTheme.neutralColor.light,
			arrangement = Arrangement.Center,
			dividerColor = Color.Transparent,
			onPickerClick = { picker ->
				when (picker) {
					NoteSegment.LIST -> {
						holdingNotePicker = picker as? NoteSegment
						if(pageState.currentPage != 0) {
							coroutineScope.launch {
								pageState.animateScrollToPage(0)
							}
						}
					}
					
					NoteSegment.CHECK -> {
						holdingNotePicker = picker as? NoteSegment
						if(pageState.currentPage != 1) {
							coroutineScope.launch {
								pageState.animateScrollToPage(1)
							}
						}
					}
				}
			}
		)
		
		AioNoteFilter(
			modifier =  Modifier.fillMaxWidth().padding(12.dp),
			filerList = listOf(
			"one",
			"two",
			"three",
			"four",
			"one",
			"two",
			"three",
			"four",
			"one",
			"two",
			"three",
			"four"
		), holdingNotePicker = null)
		
		HorizontalPager(
			pageCount = 2,
			state = pageState
		) { page ->
			if (page == 0) {
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
									NoteToolbarItem.DELETE -> onEvent(HomeEvent.DeleteNote(index))
								}
							}
						)
					}
				}
			} else {
				LazyColumn(
					modifier = modifier,
					contentPadding = PaddingValues(12.dp),
					verticalArrangement = Arrangement.spacedBy(12.dp),
					horizontalAlignment = Alignment.CenterHorizontally
				) {
					itemsIndexed(homeUiState.listTask) { index, note ->
						AioTaskPreview(
							note = note,
							onNoteClick = { onEvent(HomeEvent.NavigateToEditTask(it)) },
							onToolbarItemClick = { toolbar ->
								when (toolbar) {
									NoteToolbarItem.DELETE -> onEvent(HomeEvent.DeleteTask(index))
								}
							}
						)
					}
				}
			}
		}
	}
}

@Preview
@Composable
private fun PreviewHomeRoute() {
	AioComposeTheme {
		HomeRoute( onChangeCurrentPage = {}, navigateToNote = {}) {}
	}
}