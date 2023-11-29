package com.notes.aionote

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

class FileProviderHelper: FileProvider() {
	companion object {
		fun createUriForImage(context: Context): Uri {
			val directory = File(context.cacheDir, "image")
			directory.mkdirs()
			val file = File.createTempFile(
				"image_",
				".jpg",
				directory,
			)
			val authority = context.packageName + ".file_provider"
			return getUriForFile(
				context,
				authority,
				file,
			)
		}
		
		fun createUriForVideo(context: Context): Uri {
			val directory = File(context.cacheDir, "video")
			directory.mkdirs()
			val file = File.createTempFile(
				"video_",
				".mp4",
				directory,
			)
			val authority = context.packageName + ".file_provider"
			return getUriForFile(
				context,
				authority,
				file,
			)
		}
	}
}