package com.notes.aionote.presentation.category

import android.util.Log
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.notes.aionote.R
import com.notes.aionote.collectInLaunchedEffectWithLifecycle
import com.notes.aionote.presentation.category.components.AioCategoryCell
import com.notes.aionote.presentation.note.components.CategoryToolbarItem
import com.notes.aionote.ui.component.AioActionBar
import com.notes.aionote.ui.component.AioCornerCard
import com.notes.aionote.ui.theme.AioComposeTheme
import com.notes.aionote.ui.theme.AioTheme

@Composable
fun CategoryRoute(
	onBackClick: () -> Unit,
	navigateToHomeWithFilter: (String) -> Unit,
	categoryViewModel: CategoryViewModel = hiltViewModel()
) {
	val categoryUiState by categoryViewModel.uiState.collectAsStateWithLifecycle()
	
	CategoryScreen(
		modifier = Modifier
			.fillMaxSize()
			.background(AioTheme.neutralColor.light),
		categoryUiState = categoryUiState,
		onEvent = categoryViewModel::onEvent,
		onBackClick = onBackClick
	)
	
	AnimatedVisibility(visible = categoryUiState.isCreatingCategory) {
		CreatingCategoryBottomSheet(
			categoryUiState = categoryUiState,
			onEvent = categoryViewModel::onEvent,
		)
	}
	
	categoryViewModel.oneTimeEvent.collectInLaunchedEffectWithLifecycle { categoryOneTimeEvent ->
		when (categoryOneTimeEvent) {
			is CategoryOneTimeEvent.OnFilterNote -> {
				categoryOneTimeEvent.category.category?.let {
					navigateToHomeWithFilter.invoke(it)
				}
			}
			else -> { /*noop*/
			}
		}
	}
}

@Composable
fun CategoryScreen(
	modifier: Modifier = Modifier,
	categoryUiState: CategoryUiState,
	onEvent: (CategoryEvent) -> Unit,
	onBackClick: () -> Unit,
) {
	Column(modifier = modifier.padding(12.dp)) {
		AioActionBar(leadingIconClick = onBackClick) {
			Text(text = stringResource(id = R.string.category))
		}
		LazyColumn(
			modifier = Modifier.weight(1f),
			verticalArrangement = Arrangement.spacedBy(8.dp),
			contentPadding = PaddingValues(vertical = 8.dp)
		) {
			itemsIndexed(categoryUiState.categoryList) { index, category ->
				AioCategoryCell(
					category = category.category ?: "",
					isHolding = categoryUiState.currentCategory?.categoryId == category.categoryId,
					onCategoryClick = {
						onEvent.invoke(CategoryEvent.OnCategoryClick(category))
					},
					onToolbarItemClick = { toolbar ->
						when (toolbar) {
							CategoryToolbarItem.DELETE -> onEvent(
								CategoryEvent.OnDeleteCategory(
									category.categoryId
								)
							)
						}
					}
				)
			}
		}
		
		Spacer(modifier = Modifier.height(8.dp))
		
		AioCornerCard(
			modifier = Modifier
				.wrapContentSize()
				.fillMaxWidth()
				.clickable(
					interactionSource = remember { MutableInteractionSource() },
					indication = rememberRipple(),
					enabled = true,
					onClick = {
						onEvent.invoke(CategoryEvent.OnCreateCategoryClick)
					}
				)
		) {
			Column(
				modifier = Modifier.padding(12.dp),
				horizontalAlignment = Alignment.CenterHorizontally,
				verticalArrangement = Arrangement.spacedBy(12.dp)
			) {
				Icon(
					painter = painterResource(id = R.drawable.folder_add_outline),
					contentDescription = "",
					tint = AioTheme.successColor.base
				)
				Text(
					text = stringResource(id = R.string.new_category),
					style = AioTheme.regularTypography.base
				)
			}
		}
	}
	
}

@Preview
@Composable
private fun PreviewCategoryScreen() {
	AioComposeTheme {
		CategoryScreen(onBackClick = {}, onEvent = {}, categoryUiState = CategoryUiState())
	}
}