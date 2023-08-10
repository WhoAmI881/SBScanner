package com.example.sbscanner.data.repository

import com.example.sbscanner.data.source.local.DocumentLocalDataSource
import com.example.sbscanner.data.source.remote.DocumentRemoteDataSource
import com.example.sbscanner.domain.models.Document
import com.example.sbscanner.domain.models.FullDocument
import com.example.sbscanner.domain.repository.DocumentRepository
import kotlinx.coroutines.flow.Flow

class DocumentRepositoryImpl(
    private val documentLocalDataSource: DocumentLocalDataSource,
    private val documentRemoteDataSource: DocumentRemoteDataSource
) : DocumentRepository {

    override fun getFullDocumentsFlowByBoxId(boxId: Int): Flow<List<FullDocument>> {
        return documentLocalDataSource.getFullDocumentsFlowByBoxId(boxId)
    }

    override suspend fun getFullDocumentsByBoxId(boxId: Int): List<FullDocument> {
        return documentLocalDataSource.getFullDocumentsByBoxId(boxId)
    }

    override suspend fun getDocumentsByBoxId(boxId: Int): List<Document> {
        return documentLocalDataSource.getDocumentsByBoxId(boxId)
    }

    override suspend fun updateDocument(boxId: Int, document: Document) {
        documentLocalDataSource.updateDocument(boxId, document)
    }

    override suspend fun addDocument(boxId: Int, document: Document): Int {
        return documentLocalDataSource.addDocument(boxId, document)
    }

    override suspend fun removeDocument(docId: Int) {
        documentLocalDataSource.removeDocument(docId)
    }

    override suspend fun getDocumentById(docId: Int): Document? {
        return documentLocalDataSource.getDocumentById(docId)
    }

    override suspend fun getDocumentByParams(boxId: Int, docBarcode: String): Document? {
        return documentLocalDataSource.getDocumentByParams(boxId, docBarcode)
    }

    override suspend fun getDocumentByBarcode(docBarcode: String): Document? {
        return documentLocalDataSource.getDocumentByBarcode(docBarcode)
    }
}
