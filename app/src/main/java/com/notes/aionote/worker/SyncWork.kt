package com.notes.aionote.worker

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import android.text.format.DateUtils
import android.view.View
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.notes.aionote.MainActivity
import com.notes.aionote.R
import com.notes.aionote.common.AioConst
import com.notes.aionote.common.FirebaseConst
import com.notes.aionote.common.fail
import com.notes.aionote.common.success
import com.notes.aionote.domain.repository.SyncRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class SyncWork @AssistedInject constructor(
	@Assisted context: Context,
	@Assisted params: WorkerParameters,
	private val syncRepository: SyncRepository
): CoroutineWorker(context, params) {
	override suspend fun doWork(): Result {
		val userId = inputData.getString(FirebaseConst.FIREBASE_SYNC_USER_ID) ?: ""
		sendNotification(AioConst.NOTIFICATION_SYNC_ID, "Syncing")
		syncRepository.sync(userId).success {
			sendNotification(AioConst.NOTIFICATION_SYNC_ID, "Sync Success")
			return Result.success()
		}.fail {
			sendNotification(AioConst.NOTIFICATION_SYNC_ID, "Sync Fail $it")
			return Result.failure()
		}
		return Result.success()
	}
	
	private fun sendNotification(
		id: Int,
		title: String = "",
		messageBody: String = ""
	) {
		val intent = Intent(applicationContext, MainActivity::class.java)
		intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
		intent.putExtra(AioConst.NOTIFICATION_ID, id)
		
		val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
			PendingIntent.getActivity(applicationContext, 0, intent, PendingIntent.FLAG_MUTABLE)
		} else {
			PendingIntent.getActivity(
				applicationContext,
				0,
				intent,
				PendingIntent.FLAG_UPDATE_CURRENT
			)
		}
		
		val defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
		
		val notificationLayout = RemoteViews(
			applicationContext.packageName,
			R.layout.aio_notification
		).apply {
			setViewVisibility(R.id.tv_title, if (title.isEmpty()) View.GONE else View.VISIBLE)
			setViewVisibility(
				R.id.tv_message,
				if (messageBody.isEmpty()) View.GONE else View.VISIBLE
			)
			
			setTextViewText(R.id.tv_title, title)
			setTextViewText(R.id.tv_message, messageBody)
			
			setTextViewText(
				R.id.tv_time,
				DateUtils.formatDateTime(
					applicationContext,
					System.currentTimeMillis(),
					DateUtils.FORMAT_SHOW_TIME
				)
			)
		}
		
		val notification = NotificationCompat.Builder(applicationContext,
		                                              AioConst.NOTIFICATION_CHANNEL
		)
			.setSmallIcon(R.drawable.note_gray)
			.setSound(defaultSoundUri)
			.setDefaults(NotificationCompat.DEFAULT_ALL)
			.setContentIntent(pendingIntent)
			.setAutoCancel(true)
		
		if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
			notification
				.setCustomContentView(notificationLayout)
		} else {
			notification
				.setContentTitle(title)
				.setContentText(messageBody)
				.color = Color.WHITE
		}
		
		notification.priority = NotificationCompat.PRIORITY_MAX
		val notificationManager =
			applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
		notification.setChannelId(AioConst.NOTIFICATION_CHANNEL)
		notificationManager.notify(id, notification.build())
	}
}