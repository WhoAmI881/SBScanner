package com.example.sbscanner.data.remote.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Context.NOTIFICATION_SERVICE
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkManager
import androidx.work.WorkerParameters
import com.example.sbscanner.R
import kotlinx.coroutines.delay

private const val CHANNEL_ID = "CHANNEL_ID"
private const val NOTIFICATION_ID = 13

class UploadTaskManager(context: Context, parameters: WorkerParameters) :
  CoroutineWorker(context, parameters) {

  private val notificationManager = context.getSystemService(NOTIFICATION_SERVICE) as
    NotificationManager

  private lateinit var notificationBuilder: NotificationCompat.Builder


  override suspend fun doWork(): Result {
    val progress = "Starting Download"
    setForeground(createForegroundInfo(progress))
    sending()
    return Result.success()
  }

  private suspend fun sending() {
    repeat(100) {
      delay(500)
      notifyProgress(100, it, "sending")
    }
    notifyProgress(0, 0, "Успешная отправка")
  }

  private fun notifyProgress(
    max: Int,
    progress: Int,
    content: String
  ) {
    notificationBuilder.setProgress(max, progress, false)
    notificationBuilder.setContentText(content)
    notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
  }

  private fun createForegroundInfo(progress: String): ForegroundInfo {
    val id = CHANNEL_ID
    val title = "SEND"
    val cancel = "STOP"
    val intent = WorkManager.getInstance(applicationContext).createCancelPendingIntent(getId())

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      createChannel()
    }

    notificationBuilder = NotificationCompat.Builder(applicationContext, id)
      .setContentTitle(title)
      .setTicker(title)
      .setContentText(progress)
      .setSmallIcon(R.drawable.ic_upload)
      .addAction(android.R.drawable.ic_delete, cancel, intent)
      .setOngoing(true)

    return ForegroundInfo(NOTIFICATION_ID, notificationBuilder.build())
  }

  @RequiresApi(Build.VERSION_CODES.O)
  private fun createChannel() {
    val name = "Отправка задания"
    val descriptionText = "Процесс выполнения"
    val importance = NotificationManager.IMPORTANCE_DEFAULT
    val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
      description = descriptionText
    }
    val notificationManager =
      applicationContext.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
    notificationManager.createNotificationChannel(channel)
  }

  companion object {
    const val UPLOAD_TASK_NAME = "UPLOAD_TASK_NAME"
  }
}

