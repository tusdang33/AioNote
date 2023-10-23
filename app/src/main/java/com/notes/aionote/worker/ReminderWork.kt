package com.notes.aionote.worker

import android.annotation.SuppressLint
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.PendingIntent.getActivity
import android.content.*
import android.content.Context.NOTIFICATION_SERVICE
import android.content.Intent.FLAG_ACTIVITY_CLEAR_TASK
import android.content.Intent.FLAG_ACTIVITY_NEW_TASK
import android.graphics.Color
import android.media.RingtoneManager
import android.os.Build
import android.os.Build.VERSION.SDK_INT
import android.text.format.DateUtils
import android.view.View
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.ListenableWorker.Result.success
import androidx.work.WorkerParameters
import com.notes.aionote.MainActivity
import com.notes.aionote.R
import com.notes.aionote.common.AioConst.NOTIFICATION_CHANNEL
import com.notes.aionote.common.AioConst.NOTIFICATION_ID
import com.notes.aionote.common.AioConst.NOTIFICATION_TITLE

class ReminderWork(
	context: Context,
	params: WorkerParameters
): CoroutineWorker(context, params) {
	override suspend fun doWork(): Result {
		val id = inputData.getLong(NOTIFICATION_ID, 0).toInt()
		val title = inputData.getString(NOTIFICATION_TITLE) ?: "title nek"
		sendNotification(id, title)
		return success()
	}
	
	@SuppressLint("UnspecifiedImmutableFlag")
	private fun sendNotification(
		id: Int,
		title: String = "",
		messageBody: String = ""
	) {
		val intent = Intent(applicationContext, MainActivity::class.java)
		intent.flags = FLAG_ACTIVITY_NEW_TASK or FLAG_ACTIVITY_CLEAR_TASK
		intent.putExtra(NOTIFICATION_ID, id)
		
		val pendingIntent = if (SDK_INT >= Build.VERSION_CODES.S) {
			getActivity(applicationContext, 0, intent, PendingIntent.FLAG_MUTABLE)
		} else {
			getActivity(applicationContext, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT)
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
		
		val notification = NotificationCompat.Builder(applicationContext, NOTIFICATION_CHANNEL)
			.setSmallIcon(R.drawable.note_gray)
			.setSound(defaultSoundUri)
			.setDefaults(NotificationCompat.DEFAULT_ALL)
			.setContentIntent(pendingIntent)
			.setAutoCancel(true)
		
		if (SDK_INT < Build.VERSION_CODES.S) {
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
			applicationContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
		notification.setChannelId(NOTIFICATION_CHANNEL)
		notificationManager.notify(id, notification.build())
	}
}