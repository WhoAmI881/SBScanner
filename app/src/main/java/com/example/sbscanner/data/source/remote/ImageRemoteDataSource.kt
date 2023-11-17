package com.example.sbscanner.data.source.remote

import com.example.sbscanner.domain.models.SendImageForm
import com.example.sbscanner.domain.utils.ResultWrapper

interface ImageRemoteDataSource {

    suspend fun sendImage(
        form: SendImageForm,
        bytes: ByteArray
    ): ResultWrapper<Int>
}
