package com.example.sbscanner.domain.models

import android.graphics.Bitmap
import com.example.sbscanner.domain.utils.EMPTY_ID

data class Image(
    val id: Int = EMPTY_ID,
    val path: String = "",
    val bitmap: Bitmap? = null,
    val timestamp: Long = 0,
    val isSending: Boolean = false
)

data class SendImageForm(
    val sessionId: Int,
    val image: Image,
    val imgCreate: String,
    val box: Box,
    val boxCreate: String,
    val document: Document,
    val docCreate: String,
    val isLastImgInBox: Boolean
)
