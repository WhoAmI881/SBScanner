package com.example.sbscanner.domain.models

import com.example.sbscanner.domain.utils.EMPTY_ID
import com.example.sbscanner.domain.utils.toDate

data class Image(
    val id: Int = EMPTY_ID,
    val path: String = "",
    val timestamp: Long = 0,
    val isSending: Boolean = false
) {
    val dateCreate = timestamp.toDate()
}

data class SendImageForm(
    val sessionId: Int,
    val image: Image,
    val box: Box,
    val document: Document,
    val isLastImgInBox: Boolean
)
