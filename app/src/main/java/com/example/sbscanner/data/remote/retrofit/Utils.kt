package com.example.sbscanner.data.remote.retrofit

import com.example.sbscanner.domain.utils.ResultWrapper
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import retrofit2.HttpException
import java.io.IOException

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
                is IOException -> ResultWrapper.IOError(throwable.message)
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
