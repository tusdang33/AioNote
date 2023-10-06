package com.notes.aionote.domain.repository

import android.net.Uri
import java.io.File

interface AudioRecorder {
	fun startRecord(outputFile: (Uri) -> Unit)
	fun getAudioDuration(uri: Uri) : Long
	fun stopRecord()
}