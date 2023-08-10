package com.example.sbscanner.data.source.local

import com.example.sbscanner.domain.models.Task
import kotlinx.coroutines.flow.Flow

interface TaskLocalDataSource {

    suspend fun getAllTasks(): List<Task>

    suspend fun addTask(task: Task): Int

    suspend fun removeTaskById(taskId: Int)

    fun getTaskFlowById(taskId: Int): Flow<Task?>

    suspend fun getTaskById(taskId: Int): Task?

    suspend fun getTaskId(task: Task): Int

    suspend fun updateTask(task: Task)
}