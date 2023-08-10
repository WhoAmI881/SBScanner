package com.example.sbscanner.domain.usecase

import com.example.sbscanner.domain.repository.TaskRepository
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filterNotNull

class GetTaskUseCase(
    private val taskRepository: TaskRepository
) {

    operator fun invoke(taskId: Int) = taskRepository.getTaskFlowById(taskId)
        .filterNotNull()
        .distinctUntilChanged()
}
