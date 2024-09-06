package com.example.cginvoice.data

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.example.cginvoice.data.repository.user.UserRepository
import javax.inject.Inject

class CustomWorkerFactory @Inject constructor(private val userRepository: UserRepository) :
    WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? = SyncDataWorker(userRepository, appContext, workerParameters)
}