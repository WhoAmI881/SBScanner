package com.example.sbscanner.data.local.source

import com.example.sbscanner.data.local.db.dao.DocumentsDao
import com.example.sbscanner.data.local.db.entities.document.toDomain
import com.example.sbscanner.data.local.db.entities.document.toLocal
import com.example.sbscanner.data.source.local.DocumentLocalDataSource
import com.example.sbscanner.domain.models.Document
import com.example.sbscanner.domain.models.FullDocument
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DocumentLocalDataSourceImpl(
    private val documentsDao: DocumentsDao
) : DocumentLocalDataSource {

    override fun getFullDocumentsFlowByBoxId(boxId: Int): Flow<List<FullDocument>> {
        return documentsDao.getFullDocumentsFlowByBoxId(boxId)
            .map { it.map { item -> item.toDomain() } }
    }

    override suspend fun getFullDocumentsByBoxId(boxId: Int): List<FullDocument> {
        return documentsDao.getFullDocumentsByBoxId(boxId).map { it.toDomain() }
    }

    override suspend fun getDocumentsByBoxId(boxId: Int): List<Document> {
        return documentsDao.getDocumentsByBoxId(boxId).map { it.toDomain() }
    }

    override suspend fun updateDocument(boxId: Int, document: Document) {
        documentsDao.updateDocument(document.toLocal(boxId))
    }

    override suspend fun addDocument(boxId: Int, document: Document): Int {
        return documentsDao.insertDocument(document.toLocal(boxId)).toInt()
    }

    override suspend fun removeDocument(docId: Int) {
        documentsDao.deleteDocument(docId)
    }

    override suspend fun getDocumentById(docId: Int): Document? {
        return documentsDao.getDocumentById(docId)?.toDomain()
    }
}
