package com.example.sbscanner.domain.usecase

import com.example.sbscanner.domain.repository.BoxRepository
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest

class GetFullBoxListUseCase(
    private val boxRepository: BoxRepository,
) {

    operator fun invoke(taskId: Int) =
        boxRepository.getFullBoxesFlowByTaskId(taskId).distinctUntilChanged()
}
