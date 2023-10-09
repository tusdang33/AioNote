package com.notes.aionote.data.repository

import android.content.Context
import android.media.MediaPlayer
import android.net.Uri
import com.notes.aionote.common.Resource
import com.notes.aionote.domain.repository.AudioPlayer

class AudioPlayerImpl(private val context: Context): AudioPlayer {
	private var player: MediaPlayer? = null
	
	override fun playFile(uri: Uri) {
		MediaPlayer.create(context, uri).apply {
			player = this
			start()
		}
	}
	
	override fun getPlayingAudio(): Resource<MediaPlayer> {
		return if (player != null) {
			Resource.Success(player!!)
		} else {
			Resource.Fail(errorMessage = "")
		}
	}
	
	override fun stop() {
		player?.stop()
		player?.release()
		player = null
	}
}