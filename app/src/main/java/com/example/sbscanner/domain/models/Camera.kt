package com.example.sbscanner.domain.models

interface Camera {
    suspend fun startScanning(regex: Regex?): String
    suspend fun takePhoto(): ByteArray
    suspend fun takePhotoString(): String
    suspend fun startPreview()
}
