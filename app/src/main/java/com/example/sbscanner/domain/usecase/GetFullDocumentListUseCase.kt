package com.example.sbscanner.domain.usecase

import com.example.sbscanner.domain.repository.DocumentRepository
import kotlinx.coroutines.flow.distinctUntilChanged

class GetFullDocumentListUseCase(
    private val documentRepository: DocumentRepository
) {

    operator fun invoke(boxId: Int) = documentRepository.getFullDocumentsFlowByBoxId(boxId)
        .distinctUntilChanged()
}

