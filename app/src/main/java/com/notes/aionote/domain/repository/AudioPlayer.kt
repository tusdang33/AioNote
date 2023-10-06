package com.notes.aionote.domain.repository

import android.media.MediaPlayer
import android.net.Uri
import java.io.File

interface AudioPlayer {
	fun playFile(uri: Uri)
	fun getPlayingAudio() : MediaPlayer?
	fun stop()
}