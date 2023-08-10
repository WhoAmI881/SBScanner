package com.example.sbscanner.data.remote.retrofit.api

import com.example.sbscanner.data.remote.retrofit.models.Form
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface TaskApi {

    @Headers("Content-Type: application/json")
    @POST("proc")
    suspend fun send(
        @Body data: Form,
    ): String
}
