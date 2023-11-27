package com.notes.aionote.worker

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.notes.aionote.common.AioConst
import com.notes.aionote.common.FirebaseConst
import com.notes.aionote.common.success
import com.notes.aionote.domain.repository.SyncRepository
import javax.inject.Inject

//class SyncWork @Inject constructor (
//	context: Context,
//	params: WorkerParameters,
//	private val syncRepository: SyncRepository
//): CoroutineWorker(context, params) {
//	override suspend fun doWork(): Result {
//		val userId = inputData.getString(FirebaseConst.FIREBASE_SYNC_USER_ID) ?: return Result.failure()
//		syncRepository.syncToRemote(userId).success {
//			return Result.success()
//		}
//		return Result.failure()
//	}
//}