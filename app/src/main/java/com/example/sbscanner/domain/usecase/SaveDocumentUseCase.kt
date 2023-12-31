package com.example.sbscanner.domain.usecase

import com.example.sbscanner.domain.models.Document
import com.example.sbscanner.domain.repository.DocumentRepository
import com.example.sbscanner.domain.utils.getCurrentTimestamp
import com.example.sbscanner.domain.utils.isEmptyId

class SaveDocumentUseCase(
    private val documentRepository: DocumentRepository
) {

    suspend operator fun invoke(boxId: Int, document: Document): Int {
        return if (document.id.isEmptyId()) {
            documentRepository.addDocument(boxId, document.copy(timestamp = getCurrentTimestamp()))
        } else {
            val doc = documentRepository.getDocumentById(document.id) ?: return document.id
            documentRepository.updateDocument(boxId, document.copy(timestamp = doc.timestamp))
            document.id
        }
    }
}
