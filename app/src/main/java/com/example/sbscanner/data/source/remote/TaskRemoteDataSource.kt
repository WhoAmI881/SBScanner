package com.example.sbscanner.data.source.remote

import com.example.sbscanner.domain.models.Task
import com.example.sbscanner.domain.utils.ResultWrapper

interface TaskRemoteDataSource {

    suspend fun sendTask(task: Task): ResultWrapper<Int>
}
