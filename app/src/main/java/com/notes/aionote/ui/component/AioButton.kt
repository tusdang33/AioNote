package com.notes.aionote.ui.component

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.requiredHeightIn
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.notes.aionote.R
import com.notes.aionote.ui.theme.AioComposeTheme
import com.notes.aionote.ui.theme.AioTheme

@Composable
fun AioButton(
	modifier: Modifier = Modifier,
	onClick: () -> Unit,
	enable: Boolean = true,
	enableColor: Color = AioTheme.primaryColor.base,
	disableColor: Color = AioTheme.neutralColor.base,
	borderColor: Color = Color.Transparent,
	shape: Shape = RoundedCornerShape(size = 100.dp),
	contentPaddingValues: PaddingValues = ButtonDefaults.ContentPadding,
	interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
	leadingIcon: @Composable (RowScope.() -> Unit)? = null,
	trailingIcon: @Composable (RowScope.() -> Unit)? = null,
	content: @Composable () -> Unit
) {
	
	val enabled by remember(enable) {
		mutableStateOf(enable)
	}
	
	val backgroundColor by animateColorAsState(
		targetValue = when (enabled) {
			true -> enableColor
			false -> disableColor
		}, label = ""
	)
	
	Box(
		propagateMinConstraints = true,
		modifier = Modifier
			.requiredHeightIn(54.dp)
			.then(modifier)
			.clip(shape)
			.border(1.dp, borderColor,shape)
			.background(backgroundColor)
			.clickable(
				interactionSource = interactionSource,
				indication = rememberRipple(),
				enabled = enabled,
				role = Role.Button,
				onClick = onClick
			)
			.padding(contentPaddingValues)
	) {
		CompositionLocalProvider(LocalTextStyle provides AioTheme.regularTypography.base) {
			ConstraintLayout(
				modifier = Modifier
					.fillMaxWidth()
			) {
				val (leadRef, trailRef, contentRef) = createRefs()
				
				Box(
					contentAlignment = Alignment.CenterStart,
					modifier = Modifier.constrainAs(leadRef) {
						centerVerticallyTo(parent)
						start.linkTo(parent.start)
					}
				) {
					if (leadingIcon != null) {
						Row(
							horizontalArrangement = Arrangement.Center,
							verticalAlignment = Alignment.CenterVertically,
							content = leadingIcon
						)
					}
				}
				
				Box(
					modifier = Modifier.constrainAs(trailRef) {
						centerVerticallyTo(parent)
						end.linkTo(parent.end)
					}
				) {
					if (trailingIcon != null) {
						Row(
							horizontalArrangement = Arrangement.Center,
							verticalAlignment = Alignment.CenterVertically,
							content = trailingIcon
						)
					}
				}
				
				Box(modifier = Modifier.constrainAs(contentRef) {
					centerTo(parent)
				}) {
					CompositionLocalProvider(LocalTextStyle provides AioTheme.mediumTypography.base) {
						content()
					}
				}
			}
			
		}
	}
}

@Preview
@Composable
private fun PreviewAioButton() {
	var he by remember {
		mutableStateOf(true)
	}
	AioComposeTheme {
		CompositionLocalProvider(LocalContentColor provides AioTheme.neutralColor.white) {
			AioButton(
				borderColor = AioTheme.neutralColor.black,
				enableColor = AioTheme.neutralColor.white,
				enable = he,
				onClick = { he = !he },
				trailingIcon = {
					Icon(
						painter = painterResource(id = R.drawable.arrow_left),
						contentDescription = ""
					)
				},
				leadingIcon = {
					Icon(
						painter = painterResource(id = R.drawable.arrow_left),
						contentDescription = ""
					)
				}
			) {
				Text(text = "Hahahahahdasdasdasdasdhha")
			}
		}
	}
}