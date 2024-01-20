package com.notes.aionote.presentation.note.conflicted_note

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.notes.aionote.R
import com.notes.aionote.presentation.note.components.AioGridNotePreview
import com.notes.aionote.ui.component.AioButton
import com.notes.aionote.ui.component.AioIconButton
import com.notes.aionote.ui.theme.AioTheme

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ColumnScope.ConflictedNotePicking(
	listConflictedNote: List<PickingConflictNote>,
	onAcceptLocal: () -> Unit,
	onAcceptRemote: () -> Unit,
	onResolve: () -> Unit,
	onPickNote: (Int) -> Unit,
	onPreviewNote: (Int) -> Unit,
	enabledResolve: Boolean
) {
	Row(
		modifier = Modifier.fillMaxWidth(),
		horizontalArrangement = Arrangement.SpaceAround,
		verticalAlignment = Alignment.CenterVertically
	) {
		AioIconButton(
			onClick = onAcceptLocal
		) {
			Text(
				text = stringResource(id = R.string.accept_local),
				style = AioTheme.mediumTypography.sm.copy(color = AioTheme.primaryColor.base)
			)
		}
		
		AioIconButton(
			onClick = onAcceptRemote
		) {
			Text(
				text = stringResource(id = R.string.accept_remote),
				style = AioTheme.mediumTypography.sm.copy(color = AioTheme.primaryColor.base)
			)
		}
	}
	
	Divider(modifier = Modifier.fillMaxWidth())
	
	Box(
		modifier = Modifier.weight(1f),
		contentAlignment = Alignment.Center
	) {
		LazyVerticalStaggeredGrid(
			modifier = Modifier.fillMaxHeight(),
			columns = StaggeredGridCells.Fixed(2),
			contentPadding = PaddingValues(12.dp),
			horizontalArrangement = Arrangement.spacedBy(12.dp),
			verticalItemSpacing = 12.dp
		) {
			itemsIndexed(listConflictedNote) { index, pickingNote ->
				AioGridNotePreview(
					note = pickingNote.note,
					isPickingNote = true,
					onNoteClick = { onPreviewNote.invoke(index) },
					onPickNote = { onPickNote.invoke(index) },
					isNotePicked = pickingNote.isPicked
				)
			}
		}
		Box(
			modifier = Modifier
				.fillMaxHeight()
				.width(1.dp)
				.background(AioTheme.neutralColor.base),
		)
	}
	
	
	AioButton(
		modifier = Modifier
			.wrapContentHeight()
			.align(Alignment.End),
		borderColor = AioTheme.primaryColor.base,
		enableColor = AioTheme.neutralColor.white,
		disableColor = AioTheme.neutralColor.base,
		onClick = onResolve,
		enable = enabledResolve
	) {
		Text(
			text = stringResource(id = R.string.resolve_conflict),
			style = AioTheme.mediumTypography.sm.copy(color = if (enabledResolve) AioTheme.primaryColor.base else AioTheme.neutralColor.dark)
		)
	}
}