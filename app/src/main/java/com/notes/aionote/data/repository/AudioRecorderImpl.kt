package com.notes.aionote.data.repository

import android.content.Context
import android.media.MediaMetadataRetriever
import android.media.MediaRecorder
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.core.net.toUri
import com.notes.aionote.domain.repository.AudioRecorder
import java.io.File
import java.io.FileOutputStream
import javax.inject.Inject

class AudioRecorderImpl @Inject constructor(
	private val context: Context
): AudioRecorder {
	
	private var recorder: MediaRecorder? = null
	
	@Suppress("DEPRECATION")
	private fun createRecorder(): MediaRecorder {
		return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
			MediaRecorder(context)
		} else MediaRecorder()
	}
	
	override fun startRecord(outputFile: (Uri) -> Unit) {
		File(context.cacheDir, "voice_record_${System.currentTimeMillis()}.mp3").also { file ->
			createRecorder().apply {
				setAudioSource(MediaRecorder.AudioSource.MIC)
				setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
				setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
				setOutputFile(FileOutputStream(file).fd)
				
				prepare()
				start()
				recorder = this
				outputFile.invoke(file.toUri())
			}
		}
	}
	
	override fun getAudioDuration(uri: Uri): Long {
		val mediaMetadataRetriever = MediaMetadataRetriever();
		mediaMetadataRetriever.setDataSource(context, uri);
		return mediaMetadataRetriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
			?.toLong() ?: 0L
	}
	
	override fun stopRecord() {
		recorder?.stop()
		recorder?.reset()
		recorder = null
	}
}