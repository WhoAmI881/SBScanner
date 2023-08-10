package com.example.sbscanner.presentation.fragments.task.upload

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.sbscanner.data.remote.service.ServiceActions
import com.example.sbscanner.data.remote.service.UploadService

class UploadReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == ServiceActions.STOP_SEND.action) {
            UploadService.stopService(context)
            LocalBroadcastManager.getInstance(context)
                .sendBroadcast(Intent(intent.action))
        }
    }
}
