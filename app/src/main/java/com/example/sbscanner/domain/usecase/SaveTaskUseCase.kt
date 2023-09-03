package com.example.sbscanner.domain.usecase

import com.example.sbscanner.domain.models.Task
import com.example.sbscanner.domain.repository.TaskRepository
import com.example.sbscanner.domain.utils.getCurrentTimestamp
import com.example.sbscanner.domain.utils.isEmptyId
import com.example.sbscanner.domain.utils.isNotEmptyId

sealed class SaveTaskResult {
    class TaskAdded(val taskId: Int) : SaveTaskResult()
    class TaskUpdated(val taskId: Int) : SaveTaskResult()
    object TaskAlreadyExists : SaveTaskResult()
}

class SaveTaskUseCase(
    private val taskRepository: TaskRepository
) {

    suspend operator fun invoke(task: Task): SaveTaskResult {
        val taskId = taskRepository.getTaskId(task)
        return when {
            taskId.isNotEmptyId() && taskId != task.id -> {
                SaveTaskResult.TaskAlreadyExists
            }
            task.id.isEmptyId() -> {
                val id = taskRepository.addTask(task.copy(timestamp = getCurrentTimestamp()))
                SaveTaskResult.TaskAdded(id)
            }
            else -> {
                taskRepository.updateTask(task)
                SaveTaskResult.TaskUpdated(task.id)
            }
        }
    }
}
