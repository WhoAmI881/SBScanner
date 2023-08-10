package com.example.sbscanner.data.source.remote

import android.graphics.Bitmap
import com.example.sbscanner.domain.models.SendImageForm
import com.example.sbscanner.domain.utils.ResultWrapper

interface ImageRemoteDataSource {

    suspend fun sendImage(
        form: SendImageForm,
        bitmap: Bitmap
    ): ResultWrapper<Int>
}
