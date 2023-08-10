package com.example.sbscanner.domain.usecase

import com.example.sbscanner.domain.models.Task
import com.example.sbscanner.domain.repository.TaskRepository

class GetTaskListUseCase(
    private val taskRepository: TaskRepository
) {

    suspend operator fun invoke(): List<Task> {
        return taskRepository.getAllTasks()
    }
}
