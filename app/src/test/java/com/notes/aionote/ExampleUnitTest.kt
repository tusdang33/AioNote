package com.notes.aionote

import android.os.Build
import org.junit.Test

import org.junit.Assert.*

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {
	@Test
	fun addition_isCorrect() {
		assertEquals(4, 2 + 2)
	}
	
	@Test
	fun `check format of time stamp format`() {
		val longInstance = 1695978793L
		val actual = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			longInstance.formatTimestamp(yearTimePattern)
		} else {
			""
		}
		assertEquals("00:00:03", actual)
	}
}