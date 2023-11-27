package com.notes.aionote.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import com.notes.aionote.R
import com.notes.aionote.ui.component.AioActionBarDefaults.MinHeight
import com.notes.aionote.ui.theme.AioComposeTheme
import com.notes.aionote.ui.theme.AioTheme

@Composable
fun AioActionBar(
	modifier: Modifier = Modifier,
	leadingIconClick: () -> Unit,
	leadingIcon: @Composable (() -> Unit)? = null,
	trailingIcon: @Composable (RowScope.() -> Unit)? = null,
	content: @Composable () -> Unit,
) {
	Column {
		ConstraintLayout(
			modifier = modifier
				.fillMaxWidth()
				.sizeIn(minHeight = MinHeight)
		) {
			val (leadRef, trailRef, contentRef) = createRefs()
			
			Box(
				contentAlignment = Alignment.CenterStart,
				modifier = Modifier.constrainAs(leadRef) {
					centerVerticallyTo(parent)
					start.linkTo(parent.start)
				}
			) {
				if(leadingIcon != null) {
					AioIconButton(
						onClick = leadingIconClick,
						contentPaddingValues = PaddingValues(4.dp)
					) {
						leadingIcon()
					}
				} else {
					AioIconButton(
						onClick = leadingIconClick
					) {
						Icon(
							painter = painterResource(id = R.drawable.arrow_left_outline),
							contentDescription = ""
						)
						Spacer(modifier = Modifier.width(15.dp))
						Text(text = "Back", style = AioTheme.mediumTypography.base)
					}
					
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
		Divider(modifier = Modifier.fillMaxWidth(), color = AioTheme.neutralColor.base)
	}
	
}

object AioActionBarDefaults {
	val PaddingHorizontal = 12.dp
	val MinHeight = 60.dp
}

@Preview(showBackground = true)
@Composable
fun PreviewActionBar() {
	AioComposeTheme {
		AioActionBar(
			modifier = Modifier.fillMaxWidth(),
			leadingIconClick = {},
			trailingIcon = {
				AioIconButton(
					modifier = Modifier,
					onClick = {}
				)
				{
					Image(
						painter = painterResource(id = R.drawable.arrow_left),
						contentDescription = ""
					)
				}
			}
		) {
			Text(
				text = "New Notes",
				style = AioTheme.regularTypography.base.copy(fontWeight = FontWeight.Bold)
			)
		}
	}
}