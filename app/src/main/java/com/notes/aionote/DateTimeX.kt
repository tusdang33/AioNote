package com.notes.aionote

import android.os.Build
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

const val yearTimePattern = "yyyy-MM-dd HH:mm:ss"
const val hourTimePattern = "HH:mm:ss"
const val dayTimePattern = "yyyy-MM-dd"

fun Long.formatTimestamp(pattern: String = yearTimePattern): String {
	return if (pattern == hourTimePattern) {
		val dateFormat = SimpleDateFormat(pattern, Locale.CHINESE)
		dateFormat.timeZone = java.util.TimeZone.getTimeZone("UTC")
		dateFormat.format(this)
	} else {
		val instant = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			Instant.ofEpochMilli(this)
		} else {
			TODO("VERSION.SDK_INT < O")
		}
		val dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault())
		val formatter = DateTimeFormatter.ofPattern(pattern)
		dateTime.format(formatter)
	}
}

fun formatTimeString(hour: Int, min: Int): String =
	String.format("%02d:%02d", hour, min)