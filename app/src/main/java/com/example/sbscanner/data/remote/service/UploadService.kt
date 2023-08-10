package com.example.sbscanner.data.remote.service

import android.app.*
import android.content.Context
import android.content.Intent
import android.os.*
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.sbscanner.App
import com.example.sbscanner.R
import com.example.sbscanner.domain.usecase.SendTaskResult
import com.example.sbscanner.domain.usecase.SendTaskUseCase
import com.example.sbscanner.presentation.fragments.task.upload.UploadReceiver
import kotlinx.coroutines.*
import java.util.*

private const val CHANNEL_ID = "CHANNEL_ID"
private const val NOTIFICATION_ID = 13

class UploadService : Service() {

    private lateinit var notificationManager: NotificationManagerCompat
    private lateinit var notificationBuilder: NotificationCompat.Builder

    private lateinit var sendTaskUseCase: SendTaskUseCase
    private lateinit var wakeLock: PowerManager.WakeLock

    private val job = SupervisorJob()
    private val scope = CoroutineScope(Dispatchers.IO + job)

    override fun onCreate() {
        val powerManager = getSystemService(Context.POWER_SERVICE) as PowerManager
        wakeLock =
            powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "UploadService::WakeLock")

        createNotificationChannel()
        sendTaskUseCase = App.INSTANCE.sendTaskUseCase

        val stopIntent = Intent(this, UploadReceiver::class.java).apply {
            action = ServiceActions.STOP_SEND.action
        }
        val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            PendingIntent.getBroadcast(this, 0, stopIntent, PendingIntent.FLAG_IMMUTABLE)
        } else {
            PendingIntent.getBroadcast(this, 0, stopIntent, 0)
        }

        notificationManager = NotificationManagerCompat.from(this)
        notificationBuilder = NotificationCompat.Builder(this, CHANNEL_ID).apply {
            setContentTitle("Отправка данных")
            setContentText("")
            setSmallIcon(R.drawable.ic_upload)
            priority = NotificationCompat.PRIORITY_LOW
            addAction(R.drawable.ic_upload, "Остановить", pendingIntent)
            setCategory(NotificationCompat.CATEGORY_PROGRESS)
            setSilent(true)
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val name = "Отправка задания"
            val descriptionText = "Процесс выполнения"
            val importance = NotificationManager.IMPORTANCE_DEFAULT
            val channel = NotificationChannel(CHANNEL_ID, name, importance).apply {
                description = descriptionText
            }
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        wakeLock.acquire()
        val sessionId = intent.getIntExtra(KEY_SESSION, 0)
        val taskId = intent.getIntExtra(KEY_TASK, 0)

        startForeground(NOTIFICATION_ID, notificationBuilder.build())
        val manager = LocalBroadcastManager.getInstance(applicationContext)

        scope.launch {
            val timeStart = Calendar.getInstance().timeInMillis
            var imgMaxCount = 0
            sendTaskUseCase(sessionId, taskId).collect {
                when (it) {
                    is SendTaskResult.StartUploadTask -> {
                        imgMaxCount = it.imgMaxCount
                        manager.sendBroadcast(
                            Intent(ServiceActions.START_SEND.action).apply {
                                putExtra(ServiceActions.START_SEND.action, imgMaxCount)
                            }
                        )
                        notificationBuilder.notifyProgress(
                            imgMaxCount,
                            0,
                            "фото: 0 из $imgMaxCount"
                        )
                    }
                    is SendTaskResult.SentImage -> {
                        manager.sendBroadcast(
                            Intent(ServiceActions.SENT_IMAGE.action).apply {
                                putExtra(
                                    ServiceActions.SENT_IMAGE.action,
                                    100 * it.imgCount / imgMaxCount
                                )
                            }
                        )
                        notificationBuilder.notifyProgress(
                            imgMaxCount,
                            it.imgCount,
                            "фото: ${it.imgCount} из $imgMaxCount"
                        )
                    }
                    is SendTaskResult.Success -> {
                        manager.sendBroadcast(Intent(ServiceActions.SUCCESS_SEND.action))
                        val timeEnd = (Calendar.getInstance().timeInMillis - timeStart) / 1000
                        notificationBuilder.notifyProgress(0, 0, "Успешная отправка")
                    }
                    is SendTaskResult.LoseConnection -> {
                        manager.sendBroadcast(Intent(ServiceActions.LOSE_CONNECTION.action))
                    }
                    is SendTaskResult.ServerError -> {
                        manager.sendBroadcast(Intent(ServiceActions.SERVER_ERROR.action))
                    }
                }
            }
            //stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
        }
        return START_STICKY
    }

    override fun onDestroy() {
        if (job.isActive) job.cancel()
        wakeLock.release()
    }

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    private fun NotificationCompat.Builder.notifyProgress(
        max: Int,
        progress: Int,
        content: String
    ) {
        notificationBuilder.setProgress(max, progress, false)
        notificationBuilder.setContentText(content)
        notificationManager.notify(NOTIFICATION_ID, notificationBuilder.build())
    }

    companion object {

        private const val KEY_SESSION = "KEY_SESSION"

        private const val KEY_TASK = "KEY_TASK"

        fun startService(sessionId: Int, taskId: Int, context: Context) {
            Intent(context, UploadService::class.java).apply {
                putExtra(KEY_SESSION, sessionId)
                putExtra(KEY_TASK, taskId)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(this)
                } else {
                    context.startService(this)
                }
            }
        }

        fun stopService(context: Context) {
            Intent(context, UploadService::class.java).apply {
                context.stopService(this)
            }
        }

        @Suppress("DEPRECATION")
        fun isServiceRunning(context: Context): Boolean {
            val service: Class<UploadService> = UploadService::class.java
            return (context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager)
                .getRunningServices(Integer.MAX_VALUE)
                .any { it.service.className == service.name }
        }
    }
}
