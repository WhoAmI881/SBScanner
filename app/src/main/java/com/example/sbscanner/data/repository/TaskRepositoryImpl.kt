package com.example.sbscanner.data.repository

import com.example.sbscanner.data.source.local.TaskLocalDataSource
import com.example.sbscanner.data.source.remote.TaskRemoteDataSource
import com.example.sbscanner.domain.models.Task
import com.example.sbscanner.domain.repository.TaskRepository
import com.example.sbscanner.domain.utils.ResultWrapper
import kotlinx.coroutines.flow.Flow

class TaskRepositoryImpl(
    private val taskLocalDataSource: TaskLocalDataSource,
    private val taskRemoteDataSource: TaskRemoteDataSource
) : TaskRepository {

    override suspend fun getAllTasks(): List<Task> {
        return taskLocalDataSource.getAllTasks()
    }

    override suspend fun addTask(task: Task): Int {
        return taskLocalDataSource.addTask(task)
    }

    override suspend fun getTaskId(task: Task): Int {
        return taskLocalDataSource.getTaskId(task)
    }

    override suspend fun updateTask(task: Task) {
        taskLocalDataSource.updateTask(task)
    }

    override suspend fun removeTask(taskId: Int) {
        taskLocalDataSource.removeTaskById(taskId)
    }

    override fun getTaskFlowById(taskId: Int): Flow<Task?> {
        return taskLocalDataSource.getTaskFlowById(taskId)
    }

    override suspend fun getTaskById(taskId: Int): Task? {
        return taskLocalDataSource.getTaskById(taskId)
    }

    override suspend fun getSessionIdByTask(task: Task): ResultWrapper<Int> {
        return taskRemoteDataSource.sendTask(task)
    }
}
