package com.example.sbscanner.domain.repository

import com.example.sbscanner.domain.models.Task
import com.example.sbscanner.domain.utils.ResultWrapper
import kotlinx.coroutines.flow.Flow

interface TaskRepository {

    suspend fun getAllTasks(): List<Task>

    suspend fun addTask(task: Task): Int

    suspend fun removeTask(taskId: Int)

    fun getTaskFlowById(taskId: Int): Flow<Task?>

    suspend fun getTaskById(taskId: Int): Task?

    suspend fun getTaskId(task: Task): Int

    suspend fun updateTask(task: Task)

    suspend fun getSessionIdByTask(task: Task): ResultWrapper<Int>
}
