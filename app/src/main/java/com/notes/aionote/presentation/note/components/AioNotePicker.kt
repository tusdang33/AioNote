package com.notes.aionote.presentation.note.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.requiredHeightIn
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Divider
import androidx.compose.material3.DividerDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.notes.aionote.R
import com.notes.aionote.conditional
import com.notes.aionote.ui.component.AioIconButton
import com.notes.aionote.ui.theme.AioComposeTheme
import com.notes.aionote.ui.theme.AioTheme

@Composable
fun AioNotePicker(
	modifier: Modifier = Modifier,
	pickers: List<NotePicker>,
	holdingNotePicker: NotePicker?,
	backgroundColor: Color = AioTheme.neutralColor.white,
	dividerColor: Color = DividerDefaults.color,
	arrangement: Arrangement.Horizontal? = null,
	onToolbarItemClick: (NoteToolBar) -> Unit = {},
	onPickerClick: (NotePicker) -> Unit
) {
	var toolbarItem : List<NoteToolBar> by remember {
		mutableStateOf(listOf())
	}
	
	Column(modifier = modifier.requiredHeightIn(min = 48.dp)) {
		Divider(
			color = dividerColor
		)
		AioNoteToolbar(
			modifier = Modifier.fillMaxWidth(),
			toolbarItem = toolbarItem,
			onItemClick = onToolbarItemClick,
			showToolbar = toolbarItem.isNotEmpty(),
			elevation = 0.dp
		)
		
		Row(
			modifier = Modifier
				.fillMaxWidth()
				.background(backgroundColor),
			horizontalArrangement = arrangement ?: Arrangement.SpaceAround
		) {
			pickers.forEachIndexed { index, notePicker ->
				val holding = holdingNotePicker == notePicker
				AioIconButton(
					modifier = Modifier
						.conditional(arrangement == null) {
							weight(1f)
						},
					onClick = {
						toolbarItem = if ((notePicker == NoteOption.IMAGE || notePicker == NoteOption.VIDEO) && toolbarItem.isEmpty()) {
							when (notePicker) {
								NoteOption.IMAGE -> ImagePickerToolbarItem.values().toList()
								NoteOption.VIDEO -> VideoPickerToolbarItem.values().toList()
								else -> listOf()
							}
						} else {
							listOf()
						}
						onPickerClick.invoke(notePicker)
					}
				) {
					notePicker.icon?.let { notePickerIcon ->
						Image(
							painter = painterResource(
								id = if (holding) notePicker.altIcon
									?: notePickerIcon else notePickerIcon
							), contentDescription = ""
						)
					}
				}
				
				if (arrangement == Arrangement.Center && index != pickers.size - 1) {
					Spacer(modifier = Modifier.width(32.dp))
				}
			}
		}
	}
}

interface NotePicker {
	@get:DrawableRes
	val icon: Int?
	
	@get:DrawableRes
	val altIcon: Int?
}

enum class NoteOption: NotePicker {
	VOICE {
		override val icon: Int = R.drawable.microphone_outline
		override val altIcon: Int = R.drawable.stop_fill
	},
	CHECK {
		override val icon: Int = R.drawable.check_outline
		override val altIcon: Int? = null
	},
	IMAGE {
		override val icon: Int = R.drawable.photograph_outline
		override val altIcon: Int? = null
	},
	VIDEO {
		override val icon: Int = R.drawable.film_outline
		override val altIcon: Int? = null
	},
	ATTACHMENT {
		override val icon: Int = R.drawable.paper_clip_outline
		override val altIcon: Int? = null
	}
}

enum class NoteSegment: NotePicker {
	LIST {
		override val icon: Int = R.drawable.note_list_outline
		override val altIcon: Int = R.drawable.note_list_fill
	},
	
	CHECK {
		override val icon: Int = R.drawable.note_check_outline
		override val altIcon: Int = R.drawable.note_check_fill
	}
}

@Preview
@Composable
private fun PreviewAioNotePicker() {
	AioComposeTheme {
		AioNotePicker(
			pickers = NoteOption.values().toList(),
			onPickerClick = {},
			holdingNotePicker = null,
			onToolbarItemClick = {
			
			}
		)
	}
}