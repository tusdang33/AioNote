package com.notes.aionote.ui.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout

@Composable
fun RowScope.AioBottomNavigationItem(
	modifier: Modifier = Modifier,
	onClick: () -> Unit,
	selected: Boolean,
	icon: @Composable () -> Unit,
	enable: Boolean = true,
	label: @Composable (() -> Unit)? = null,
	showLabel: Boolean = true,
	interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {
	Box(
		modifier = modifier.weight(1f),
		contentAlignment = Alignment.Center
	) {
		
		ConstraintLayout(modifier = Modifier.padding(vertical = 8.dp)) {
			val (iconBtnRef, labelRef) = createRefs()
			Box(modifier = Modifier.constrainAs(iconBtnRef) {
				top.linkTo(parent.top)
				centerHorizontallyTo(parent)
			}) {
				icon()
				Spacer(modifier = Modifier.matchParentSize())
			}
			Box(modifier = Modifier.constrainAs(labelRef) {
				top.linkTo(iconBtnRef.bottom, (8).dp)
				centerHorizontallyTo(parent)
			}) {
				if ((showLabel || selected) && label != null) {
					label()
				}
			}
		}
		Spacer(
			modifier = Modifier.matchParentSize()
				.clickable(
					enabled = enable,
					onClick = onClick,
					role = Role.Tab,
					indication = rememberRipple(),
					interactionSource = interactionSource
				)
		)
	}
}