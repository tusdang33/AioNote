package com.notes.aionote

import android.content.Context
import android.net.Uri
import androidx.core.content.FileProvider
import java.io.File

class FileProviderHelper: FileProvider() {
	companion object {
		fun createUriForMedia(context: Context): Uri {
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
	}
}