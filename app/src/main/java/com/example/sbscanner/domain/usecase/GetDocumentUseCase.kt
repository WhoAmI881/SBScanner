package com.example.sbscanner.domain.usecase

import com.example.sbscanner.domain.models.Document
import com.example.sbscanner.domain.repository.DocumentRepository

class GetDocumentUseCase(
    private val documentRepository: DocumentRepository
) {

    suspend operator fun invoke(docId: Int): Document? {
        return documentRepository.getDocumentById(docId)
    }
}
