package com.notes.aionote.ui.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavDestination
import com.notes.aionote.R
import com.notes.aionote.common.TopLevelDestination
import com.notes.aionote.isTopLevelDestinationInHierarchy
import com.notes.aionote.ui.theme.AioTheme

@Composable
fun AioBottomNavigationBar(
	modifier: Modifier = Modifier,
	destinations: List<TopLevelDestination>,
	currentDestination: NavDestination?,
	onNavigateToDestination: (TopLevelDestination) -> Unit,
	isShowFloatingButton: Boolean,
	onFloatingButtonClick: () -> Unit,
) {
	Box(
		contentAlignment = Alignment.Center
	) {
		ConstraintLayout() {
			val (bottomBarRef, buttonRef) = createRefs()
			Row(
				modifier = modifier
					.constrainAs(bottomBarRef) {
						start.linkTo(parent.start)
						end.linkTo(parent.end)
						bottom.linkTo(parent.bottom)
					}
					.fillMaxWidth()
					.height(80.dp)
					.padding(top = 6.dp)
					.selectableGroup(),
				horizontalArrangement = Arrangement.SpaceEvenly,
				verticalAlignment = Alignment.CenterVertically
			) {
				destinations.forEach { itemNavigation ->
					val selected =
						currentDestination.isTopLevelDestinationInHierarchy(itemNavigation)
					AioBottomNavigationItem(
						modifier = Modifier,
						selected = selected,
						onClick = { onNavigateToDestination(itemNavigation) },
						icon = {
							AioIconButton(onClick = {}, backgroundColor = Color.Transparent) {
								Icon(
									painter = painterResource(
										id = if (selected) itemNavigation.selectedIconRes else itemNavigation.unselectedIconRes
									), contentDescription = "", tint =  if (selected) AioTheme.primaryColor.base else AioTheme.neutralColor.black
								)
							}
						},
						label = {
							Text(
								text = itemNavigation.nameItem,
								style = AioTheme.regularTypography.xs.copy(color = if (selected) AioTheme.primaryColor.base else AioTheme.neutralColor.dark)
							)
						}
					)
				}
			}
			
			AnimatedVisibility(
				modifier = Modifier
					.constrainAs(buttonRef) {
						start.linkTo(parent.start)
						end.linkTo(parent.end)
						translationY = (-40).dp
					}
					.size(64.dp),
				visible = isShowFloatingButton,
				enter = fadeIn(),
				exit = fadeOut()
			) {
				FloatingActionButton(
					elevation = FloatingActionButtonDefaults.bottomAppBarFabElevation(5.dp),
					shape = CircleShape,
					containerColor = AioTheme.primaryColor.base,
					onClick = onFloatingButtonClick
				) {
					Image(painter = painterResource(id = R.drawable.plus), contentDescription = "")
				}
			}
		}
		
	}
}