package com.example.sbscanner.domain.utils

import com.example.sbscanner.domain.models.FullBox
import java.text.SimpleDateFormat
import java.util.*

const val EMPTY_ID = 0

const val SUCCESS_CODE = 0

private const val PATTERN_DATE = "dd.MM.yyyy HH:mm:ss"

fun Int.isEmptyId() = this == EMPTY_ID

fun Int.isNotEmptyId() = this != EMPTY_ID

fun Int.isSuccessCode() = this == SUCCESS_CODE || this == -2

fun List<FullBox>.getSendBoxes() = this.filter { box ->
    box.documents.isNotEmpty() &&
            box.documents.all { doc -> doc.images.isNotEmpty() } &&
            box.documents.any { doc -> doc.images.any { !it.isSending } }
}

fun getCurrentTimestamp() = Calendar.getInstance().timeInMillis

sealed class ResultWrapper<out T> {

    data class SuccessResponse<out T>(val value: T) : ResultWrapper<T>()

    data class ErrorResponse(val code: Int) : ResultWrapper<Nothing>()

    object NetworkError : ResultWrapper<Nothing>()

    object ParseError : ResultWrapper<Nothing>()
}

fun Long.toDate(): String {
    val dateFormat = SimpleDateFormat(PATTERN_DATE, Locale.getDefault())
    return dateFormat.format(Date(this)).toString()
}