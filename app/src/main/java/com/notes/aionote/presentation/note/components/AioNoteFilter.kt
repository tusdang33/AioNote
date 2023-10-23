package com.notes.aionote.presentation.note.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.notes.aionote.R
import com.notes.aionote.ui.component.AioIconButton
import com.notes.aionote.ui.theme.AioComposeTheme
import com.notes.aionote.ui.theme.AioTheme

@Composable
fun AioNoteFilter(
	modifier: Modifier = Modifier,
	filerList: List<String>,
	holdingNotePicker: String?,
	onFilterClick: (String) -> Unit = {},
	onCategoryManagerClick: () -> Unit = {}
) {
	Row(modifier = modifier) {
		LazyRow(
			modifier = Modifier.weight(1f).clip(RoundedCornerShape(12.dp)),
			horizontalArrangement = Arrangement.spacedBy(10.dp)
		) {
			items(filerList) { filter ->
				val holding = holdingNotePicker == filter
				AioIconButton(
					modifier = Modifier,
					shape = RoundedCornerShape(12.dp),
					contentPaddingValues = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
					backgroundColor = if (holding) AioTheme.neutralColor.base else AioTheme.neutralColor.white,
					onClick = {
						onFilterClick.invoke(filter)
					}
				) {
					Text(
						text = filter,
						style = if (holding)
							AioTheme.boldTypography.base.copy(color = AioTheme.neutralColor.black)
						else AioTheme.regularTypography.base.copy(color = AioTheme.neutralColor.black)
					)
				}
			}
		}
		
		Spacer(modifier = Modifier.width(10.dp))
		
		AioIconButton(
			modifier = Modifier,
			shape = RoundedCornerShape(8.dp),
			contentPaddingValues = PaddingValues(vertical = 6.dp, horizontal = 10.dp),
			backgroundColor = AioTheme.neutralColor.white,
			onClick = {
				onCategoryManagerClick.invoke()
			}
		) {
			Icon(
				painter = painterResource(id = R.drawable.folder_open_outline),
				contentDescription = "",
				tint = AioTheme.primaryColor.base
			)
		}
	}
}

@Preview
@Composable
private fun PreviewAioNoteFilter() {
	AioComposeTheme {
		AioNoteFilter(
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
			),
			holdingNotePicker = null
		)
	}
}