package com.example.sbscanner.domain.repository

import com.example.sbscanner.domain.models.Document
import com.example.sbscanner.domain.models.FullDocument
import kotlinx.coroutines.flow.Flow

interface DocumentRepository {

    fun getFullDocumentsFlowByBoxId(boxId: Int): Flow<List<FullDocument>>

    suspend fun getFullDocumentsByBoxId(boxId: Int): List<FullDocument>

    suspend fun getDocumentsByBoxId(boxId: Int): List<Document>

    suspend fun updateDocument(boxId: Int, document: Document)

    suspend fun addDocument(boxId: Int, document: Document): Int

    suspend fun removeDocument(docId: Int)

    suspend fun getDocumentById(docId: Int): Document?

    suspend fun getDocumentByParams(boxId: Int, docBarcode: String): Document?

    suspend fun getDocumentByBarcode(docBarcode: String): Document?
}
