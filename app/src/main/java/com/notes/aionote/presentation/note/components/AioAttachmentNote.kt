package com.notes.aionote.presentation.note.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.notes.aionote.R
import com.notes.aionote.ui.component.AioIconButton
import com.notes.aionote.ui.theme.AioComposeTheme
import com.notes.aionote.ui.theme.AioTheme
import java.io.File

@Composable
fun AioAttachmentNote(
	modifier: Modifier = Modifier,
	attachment: File,
	backgroundColor: Color = AioTheme.primaryColor.light,
	textStyle: TextStyle = AioTheme.boldTypography.sm,
	sizeStyle: TextStyle = AioTheme.regularTypography.sm.copy(color = AioTheme.neutralColor.dark),
	readOnly: Boolean = false,
	onViewFile: () -> Unit = {},
	onDeleteClick: () -> Unit = {},
) {
	val iconSize by remember(readOnly) {
		derivedStateOf {
			if (readOnly) 12.dp else 32.dp
		}
	}
	
	val currentTextStyle by remember(readOnly) {
		derivedStateOf {
			if (readOnly) AioTheme.mediumTypography.xs else textStyle
		}
	}
	
	Row(
		modifier = modifier
			.clip(RoundedCornerShape(12.dp))
			.background(backgroundColor),
		verticalAlignment = Alignment.CenterVertically,
		horizontalArrangement = Arrangement.spacedBy(8.dp)
	) {
		Icon(
			modifier = Modifier
				.padding(start = 12.dp)
				.size(iconSize),
			painter = painterResource(id = R.drawable.paper_clip_outline), contentDescription = ""
		)
		Text(
			modifier = Modifier.weight(1f).clickable {
				onViewFile.invoke()
			},
			text = attachment.name,
			overflow = TextOverflow.Ellipsis,
			maxLines = 1,
			style = currentTextStyle
		)

//		Text(
//			text = "${attachment.length() / 1024f}Kb",
//			style = sizeStyle
//		)
		if (!readOnly)
			AioIconButton(onClick = onDeleteClick) {
				Icon(
					modifier = Modifier.size(iconSize),
					painter = painterResource(id = R.drawable.trash_fill),
					contentDescription = ""
				)
			}
	}
}

@Preview
@Composable
private fun PreviewAioAttachmentNote() {
	AioComposeTheme {
		AioAttachmentNote(attachment = File("")) {
		
		}
	}
}