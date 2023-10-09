package com.notes.aionote.domain.repository

import android.media.MediaPlayer
import android.net.Uri
import com.notes.aionote.common.Resource
import java.io.File

interface AudioPlayer {
	fun playFile(uri: Uri)
	fun getPlayingAudio() : Resource<MediaPlayer>
	fun stop()
}