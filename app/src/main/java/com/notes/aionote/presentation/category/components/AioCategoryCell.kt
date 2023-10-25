package com.notes.aionote.presentation.category.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.notes.aionote.R
import com.notes.aionote.presentation.note.components.AioNoteToolbar
import com.notes.aionote.presentation.note.components.CategoryToolbarItem
import com.notes.aionote.presentation.note.components.NoteContentToolbarItem
import com.notes.aionote.presentation.note.components.NoteToolbarItem
import com.notes.aionote.ui.component.AioCornerCard
import com.notes.aionote.ui.theme.AioComposeTheme
import com.notes.aionote.ui.theme.AioTheme

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AioCategoryCell(
	modifier: Modifier = Modifier,
	category: String,
	isHolding: Boolean = false,
	quantity: Int = 0,
	onCategoryClick: (String) -> Unit,
	onToolbarItemClick: (CategoryToolbarItem) -> Unit,
) {
	
	var showToolbar by remember {
		mutableStateOf(false)
	}
	
	AioCornerCard(
		modifier = modifier
			.combinedClickable(
				onLongClick = {
					showToolbar = !showToolbar
				},
				interactionSource = remember { MutableInteractionSource() },
				indication = rememberRipple(),
				enabled = true,
				role = Role.Button,
				onClick = {
					if (showToolbar) {
						showToolbar = false
					} else {
						onCategoryClick.invoke(category)
					}
				}
			)
			.padding(12.dp)
	) {
		Row(
			modifier = Modifier
				.fillMaxWidth()
				
				.background(AioTheme.neutralColor.white)
				.clip(RoundedCornerShape(12.dp)),
			verticalAlignment = Alignment.CenterVertically,
		) {
			if (isHolding) {
				Icon(
					painter = painterResource(id = R.drawable.check_outline),
					contentDescription = "",
					tint = AioTheme.warningColor.base
				)
			}
			Spacer(modifier = Modifier.width(10.dp))
			Text(
				modifier = Modifier.weight(1f),
				text = category,
				style = if (isHolding) AioTheme.boldTypography.base else AioTheme.regularTypography.base
			)
			Text(
				text = quantity.toString(),
				style = AioTheme.regularTypography.sm.copy(color = AioTheme.neutralColor.dark)
			)
		}
		
		AioNoteToolbar(
			toolbarItem = CategoryToolbarItem.values().toList(),
			showToolbar = showToolbar,
			onItemClick = {
				showToolbar = false
				onToolbarItemClick.invoke(it as CategoryToolbarItem)
			}
		)
	}
}

@Preview
@Composable
private fun PreviewAioCategoryCell() {
	AioComposeTheme {
		AioCategoryCell(onCategoryClick = {}, category = "All of mine", isHolding = true, onToolbarItemClick = {})
	}
}