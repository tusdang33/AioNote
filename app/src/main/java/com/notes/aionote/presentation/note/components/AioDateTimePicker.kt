package com.notes.aionote.presentation.note.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.DatePicker
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.google.accompanist.pager.HorizontalPagerIndicator
import com.notes.aionote.R
import com.notes.aionote.dayTimePattern
import com.notes.aionote.formatTimeString
import com.notes.aionote.formatTimestamp
import com.notes.aionote.ui.component.AioCornerCard
import com.notes.aionote.ui.component.AioIconButton
import com.notes.aionote.ui.theme.AioComposeTheme
import com.notes.aionote.ui.theme.AioTheme
import java.time.LocalDateTime
import java.time.ZoneId

private const val gmt7 = 25200000L
private const val hourMillis = 3600000L
private const val minuteMillis = 60000L

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun AioDateTimePicker(
	modifier: Modifier = Modifier,
	onSetDateTime: (Long) -> Unit,
	onDismissRequest: () -> Unit,
) {
	val localDateTime by remember {
		mutableStateOf(LocalDateTime.now(ZoneId.systemDefault()))
	}
	val dateState = rememberDatePickerState(initialSelectedDateMillis = System.currentTimeMillis())
	val timeState = rememberTimePickerState(
		initialHour = localDateTime.hour,
		initialMinute = localDateTime.minute,
		is24Hour = true
	)
	val localDensity = LocalDensity.current
	var currentHeight by remember {
		mutableStateOf(Dp.Unspecified)
	}
	
	val pageState = rememberPagerState()
	
	Dialog(
		properties = DialogProperties(
			usePlatformDefaultWidth = false,
		),
		onDismissRequest = onDismissRequest
	) {
		AioCornerCard(
			modifier = modifier
		) {
			Column {
				Row(
					modifier = Modifier
						.fillMaxWidth()
						.background(AioTheme.primaryColor.base)
						.padding(12.dp),
					verticalAlignment = Alignment.CenterVertically,
					horizontalArrangement = Arrangement.SpaceBetween
				) {
					Icon(
						painter = painterResource(id = R.drawable.clock_outline),
						contentDescription = "",
						tint = AioTheme.warningColor.base
					)
					Text(
						text = "${
							formatTimeString(
								timeState.hour,
								timeState.minute
							)
						}  ${dateState.selectedDateMillis?.formatTimestamp(dayTimePattern) ?: ""}",
						style = AioTheme.boldTypography.base.copy(color = AioTheme.neutralColor.white)
					)
					AioIconButton(
						backgroundColor = AioTheme.neutralColor.white,
						contentPaddingValues = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
						onClick = {
							val chosenTime = (dateState.selectedDateMillis
								?: 0L) - gmt7 + timeState.hour * hourMillis + timeState.minute * minuteMillis
							onSetDateTime.invoke(chosenTime)
						}
					) {
						Text(
							text = stringResource(id = R.string.set_button),
							style = AioTheme.regularTypography.sm.copy(color = AioTheme.primaryColor.base)
						)
					}
				}
				HorizontalPager(
					pageCount = 2,
					state = pageState
				) { page ->
					Box(
						modifier = Modifier.height(currentHeight),
						contentAlignment = Alignment.Center
					) {
						if (page == 0) {
							DatePicker(
								modifier = Modifier.onGloballyPositioned { coordinate ->
									if (currentHeight == Dp.Unspecified) {
										currentHeight = with(localDensity) {
											coordinate.size.height.toDp()
										}
									}
								},
								state = dateState
							)
						} else {
							TimePicker(state = timeState)
						}
					}
				}
				Box(
					modifier = Modifier
						.fillMaxWidth()
						.padding(bottom = 10.dp),
					contentAlignment = Alignment.Center
				) {
					HorizontalPagerIndicator(
						pagerState = pageState,
						pageCount = 2
					)
				}
			}
		}
	}
}

@Preview
@Composable
private fun PreviewAioDateTimePicker() {
	AioComposeTheme {
		AioDateTimePicker(onSetDateTime = {}) {}
	}
}