package com.example.sbscanner.data.remote.retrofit

import android.graphics.Bitmap
import android.util.Base64
import com.example.sbscanner.domain.utils.ResultWrapper
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.ByteArrayOutputStream
import java.io.IOException

fun Bitmap.toBase64(): String {
    val out = ByteArrayOutputStream()
    this.compress(Bitmap.CompressFormat.JPEG, 100, out)
    return Base64.encodeToString(out.toByteArray(), Base64.DEFAULT)
}

fun String.getResponse() = split("<ProcResult>", "</ProcResult>")
    .last { it.isNotBlank() }.toInt()

suspend fun <T> safeApiCall(
    dispatcher: CoroutineDispatcher,
    apiCall: suspend () -> T
): ResultWrapper<T> {
    return withContext(dispatcher) {
        try {
            ResultWrapper.SuccessResponse(apiCall.invoke())
        } catch (throwable: Throwable) {
            when (throwable) {
                is IOException -> ResultWrapper.NetworkError
                is HttpException -> {
                    val code = throwable.code()
                    ResultWrapper.ErrorResponse(code)
                }
                else -> {
                    ResultWrapper.ParseError
                }
            }
        }
    }
}
