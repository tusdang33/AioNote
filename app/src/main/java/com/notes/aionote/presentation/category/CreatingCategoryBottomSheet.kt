package com.notes.aionote.presentation.category

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.notes.aionote.R
import com.notes.aionote.ui.component.AioButton
import com.notes.aionote.ui.component.AioTextForm
import com.notes.aionote.ui.theme.AioComposeTheme
import com.notes.aionote.ui.theme.AioTheme

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun CreatingCategoryBottomSheet(
	modifier: Modifier = Modifier,
	categoryUiState: CategoryUiState,
	onEvent: (CategoryEvent) -> Unit,
) {
	ModalBottomSheet(
		sheetState = rememberModalBottomSheetState(),
		onDismissRequest = {
			onEvent(CategoryEvent.OnCloseCreateCategory)
		},
		windowInsets = WindowInsets.ime,
		containerColor = AioTheme.neutralColor.white
	) {
		Column(
			modifier = modifier
				.padding(18.dp)
				.clip(RoundedCornerShape(12.dp))
				.background(AioTheme.neutralColor.white)
				.padding(horizontal = 18.dp, vertical = 22.dp),
			verticalArrangement = Arrangement.spacedBy(12.dp),
			horizontalAlignment = Alignment.CenterHorizontally
		) {
			Text(
				text = stringResource(id = R.string.new_category),
				style = AioTheme.boldTypography.lg
			)
			AioTextForm(value = categoryUiState.newCategory, onValueChange = {
				onEvent(CategoryEvent.OnNewCategoryChange(it))
			})
			Row(
				modifier = Modifier.fillMaxWidth(),
				horizontalArrangement = Arrangement.SpaceEvenly,
				verticalAlignment = Alignment.CenterVertically
			) {
				AioButton(
					shape = RoundedCornerShape(22.dp),
					enableColor = AioTheme.neutralColor.white,
					borderColor = AioTheme.errorColor.base,
					onClick = {
						onEvent(CategoryEvent.OnCloseCreateCategory)
					}
				) {
					Text(text = stringResource(id = R.string.cancel))
				}
				
				AioButton(
					shape = RoundedCornerShape(22.dp),
					enableColor = AioTheme.successColor.dark,
					onClick = {
						onEvent(CategoryEvent.OnSubmitCategory)
					}
				) {
					Text(
						style = AioTheme.mediumTypography.base.copy(color = AioTheme.neutralColor.white),
						text = stringResource(id = R.string.create)
					)
				}
			}
		}
	}
}

@Preview
@Composable
private fun PreviewCreatingCategoryScreen() {
	AioComposeTheme {
		CreatingCategoryBottomSheet(categoryUiState = CategoryUiState(), onEvent = {})
	}
}