package com.notes.aionote.data.repository

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import androidx.core.net.toUri
import com.notes.aionote.domain.repository.AudioPlayer
import java.io.File

class AudioPlayerImpl(private val context: Context): AudioPlayer {
	private var player: MediaPlayer? = null
	
	override fun playFile(uri: Uri) {
		MediaPlayer.create(context, uri).apply {
			player = this
			start()
		}
	}
	
	override fun getPlayingAudio(): MediaPlayer? = this.player
	
	override fun stop() {
		player?.stop()
		player?.release()
		player = null
	}
}