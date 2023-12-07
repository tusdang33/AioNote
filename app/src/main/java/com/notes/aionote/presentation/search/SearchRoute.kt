package com.notes.aionote.presentation.search

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.notes.aionote.R
import com.notes.aionote.collectInLaunchedEffectWithLifecycle
import com.notes.aionote.presentation.note.components.AioGridNotePreview
import com.notes.aionote.presentation.note.components.NoteToolbarItem
import com.notes.aionote.ui.component.AioActionBar
import com.notes.aionote.ui.component.AioTextField
import com.notes.aionote.ui.theme.AioComposeTheme
import com.notes.aionote.ui.theme.AioTheme

@Composable
fun SearchRoute(
	onBackClick: () -> Unit,
	navigateToNote: (String) -> Unit,
	navigateToTask: (String) -> Unit,
	navigateToCategory: (String?) -> Unit,
	searchViewModel: SearchViewModel = hiltViewModel()
) {
	val searchUiState by searchViewModel.uiState.collectAsStateWithLifecycle()
	
	SearchScreen(
		modifier = Modifier.fillMaxSize(),
		searchUiState = searchUiState,
		onEvent = searchViewModel::onEvent,
		onBackClick = onBackClick
	)
	searchViewModel.oneTimeEvent.collectInLaunchedEffectWithLifecycle { searchOneTimeEvent ->
		when (searchOneTimeEvent) {
			is SearchOneTimeEvent.NavigateToCategory -> {
				navigateToCategory.invoke(searchOneTimeEvent.noteId)
			}
			is SearchOneTimeEvent.NavigateToNote -> {
				navigateToNote.invoke(searchOneTimeEvent.noteId)
			}
		}
	}
}

@OptIn(ExperimentalFoundationApi::class, ExperimentalComposeUiApi::class)
@Composable
fun SearchScreen(
	modifier: Modifier = Modifier,
	searchUiState: SearchUiState,
	onEvent: (SearchEvent) -> Unit,
	onBackClick: () -> Unit,
) {
	val focusRequester by remember {
		mutableStateOf(FocusRequester())
	}
	val keyboardController = LocalSoftwareKeyboardController.current
	Column(
		modifier = modifier
			.fillMaxSize()
			.background(AioTheme.neutralColor.white)
			.padding(12.dp),
		verticalArrangement = Arrangement.spacedBy(16.dp)
	) {
		AioActionBar(
			leadingIcon = {
				Icon(
					painter = painterResource(id = R.drawable.arrow_left_outline),
					contentDescription = null
				)
			}, leadingIconClick = onBackClick
		) {
			AioTextField(
				modifier = Modifier
					.padding(start = 32.dp)
					.height(38.dp)
					.clip(RoundedCornerShape(8.dp))
					.fillMaxWidth(),
				singleLine = true,
				contentPadding = PaddingValues(horizontal = 8.dp),
				focusRequester = focusRequester,
				textFieldColors = AioTheme.neutralColor.light,
				placeholder = {
					Box(
						modifier = Modifier
							.padding(start = 8.dp)
							.height(38.dp)
							.align(Alignment.CenterHorizontally),
						contentAlignment = Alignment.Center
					) {
						Text(
							text = stringResource(id = R.string.searching),
							style = AioTheme.regularTypography.sm.copy(color = AioTheme.neutralColor.base)
						)
					}
					
				},
				keyboardOptions = KeyboardOptions(
					autoCorrect = false,
					keyboardType = KeyboardType.Text,
					imeAction = ImeAction.Search,
				),
				keyboardActions = KeyboardActions(
					onSearch = {
						onEvent(SearchEvent.OnQuery)
						keyboardController?.hide()
					}
				
				),
				text = searchUiState.searchInput,
				onTextChange = {
					onEvent(SearchEvent.OnSearchChange(it))
				},
			)
		}
		
		LazyVerticalStaggeredGrid(
			modifier = Modifier.fillMaxHeight(),
			columns = StaggeredGridCells.Fixed(2),
			contentPadding = PaddingValues(12.dp),
			horizontalArrangement = Arrangement.spacedBy(12.dp),
			verticalItemSpacing = 12.dp
		) {
			itemsIndexed(searchUiState.searchResult) { index, note ->
				AioGridNotePreview(
					note = note,
					onNoteClick = { onEvent(SearchEvent.OnNoteClick(note.noteId)) },
					onToolbarItemClick = { toolbar ->
						when (toolbar) {
							NoteToolbarItem.DELETE -> onEvent(SearchEvent.DeleteNote(index))
							NoteToolbarItem.ADD_CATEGORY -> onEvent(
								SearchEvent.AddNoteToCategory(
									note.noteId
								)
							)
						}
					}
				)
			}
		}
	}
}

@Preview
@Composable
private fun PreviewSearchScreen() {
	AioComposeTheme {
		SearchScreen(
			searchUiState = SearchUiState(),
			onBackClick = {},
			onEvent = {}
		)
	}
}