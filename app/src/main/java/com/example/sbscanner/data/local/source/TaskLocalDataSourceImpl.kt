package com.example.sbscanner.data.local.source

import com.example.sbscanner.data.local.db.dao.TasksDao
import com.example.sbscanner.data.local.db.entities.task.toDomain
import com.example.sbscanner.data.local.db.entities.task.toLocal
import com.example.sbscanner.data.source.local.TaskLocalDataSource
import com.example.sbscanner.domain.models.Task
import com.example.sbscanner.domain.utils.EMPTY_ID
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class TaskLocalDataSourceImpl(
    private val tasksDao: TasksDao
) : TaskLocalDataSource {

    override suspend fun getAllTasks(): List<Task> {
        return tasksDao.getAllTasks().map { it.toDomain() }
    }

    override suspend fun addTask(task: Task): Int {
        return tasksDao.insertTask(task.toLocal()).toInt()
    }

    override suspend fun getTaskId(task: Task): Int {
        return tasksDao.getTaskByParams(task.userId, task.barcode)?.id ?: EMPTY_ID
    }

    override suspend fun updateTask(task: Task) {
        tasksDao.updateTask(task.toLocal())
    }

    override suspend fun removeTaskById(taskId: Int) {
        tasksDao.deleteTask(taskId)
    }

    override fun getTaskFlowById(taskId: Int): Flow<Task?> {
        return tasksDao.getTaskFlowById(taskId).map { it?.toDomain() }
    }

    override suspend fun getTaskById(taskId: Int): Task? {
        return tasksDao.getTaskById(taskId)?.toDomain()
    }
}
