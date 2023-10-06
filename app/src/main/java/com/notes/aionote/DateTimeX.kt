package com.notes.aionote

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

const val yearTimePattern = "yyyy-MM-dd HH:mm:ss"
const val hourTimePattern = "HH:mm:ss"

fun Long.formatTimestamp(pattern: String = yearTimePattern): String {
	return if(pattern == yearTimePattern) {
		val instant = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			Instant.ofEpochMilli(this)
		} else {
			TODO("VERSION.SDK_INT < O")
		}
		val dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
		val formatter = DateTimeFormatter.ofPattern(pattern)
		dateTime.format(formatter)
	} else {
		val dateFormat = SimpleDateFormat(pattern, Locale.CHINESE)
		dateFormat.timeZone = java.util.TimeZone.getTimeZone("UTC")
		dateFormat.format(this)
	}
	
}

fun DrawScope.draw() {
	drawLine(
		color= Color.Magenta,
		start = Offset(10f,10f),
		end = Offset(0f, 200f),
		strokeWidth = 12f,
		cap = StrokeCap.Round
	)
}

@Composable
fun Test() {
	Box(
		modifier = Modifier
			.size(400.dp)
			.drawBehind {
			draw()
		}
	) {
	
	}
}

@Preview
@Composable
fun hehe() {
	Test()
}