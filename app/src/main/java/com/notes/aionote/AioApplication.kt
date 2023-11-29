package com.notes.aionote

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.graphics.Color
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import androidx.hilt.work.HiltWorkerFactory
import androidx.work.Configuration
import com.notes.aionote.common.AioConst.NOTIFICATION_CHANNEL
import com.notes.aionote.common.AioConst.NOTIFICATION_NAME
import dagger.hilt.android.HiltAndroidApp
import javax.inject.Inject

@HiltAndroidApp
class AioApplication: Application(), Configuration.Provider {
	@Inject
	lateinit var workerFactory: HiltWorkerFactory
	override fun onCreate() {
		super.onCreate()
		createNotificationChannel()
	}
	
	private fun createNotificationChannel() {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
			val notificationManager =
				applicationContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
			val ringtoneManager = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
			val audioAttributes = AudioAttributes.Builder()
				.setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
				.setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
				.build()
			
			val channel = NotificationChannel(
				NOTIFICATION_CHANNEL,
				NOTIFICATION_NAME,
				IMPORTANCE_HIGH
			)
			
			channel.enableLights(true)
			channel.lightColor = Color.RED
			channel.enableVibration(true)
			channel.vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
			channel.setSound(ringtoneManager, audioAttributes)
			notificationManager.createNotificationChannel(channel)
		}
	}
	
	override fun getWorkManagerConfiguration(): Configuration {
		return Configuration.Builder()
			.setWorkerFactory(workerFactory)
			.build()
	}
}