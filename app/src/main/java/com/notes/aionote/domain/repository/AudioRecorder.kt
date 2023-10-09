package com.notes.aionote.domain.repository

import android.net.Uri
import com.notes.aionote.common.Resource
import java.io.File

interface AudioRecorder {
	fun startRecord(outputFile: (Uri) -> Unit)
	fun getAudioDuration(uri: Uri) : Resource<Long>
	fun stopRecord()
}