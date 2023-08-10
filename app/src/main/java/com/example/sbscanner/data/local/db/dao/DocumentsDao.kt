package com.example.sbscanner.data.local.db.dao

import androidx.room.*
import com.example.sbscanner.data.local.db.entities.document.DocumentDbEntity
import com.example.sbscanner.data.local.db.entities.document.FullDocumentDbEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface DocumentsDao {

    @Transaction
    @Query("SELECT * FROM documents WHERE box_id = :boxId")
    fun getFullDocumentsFlowByBoxId(boxId: Int): Flow<List<FullDocumentDbEntity>>

    @Transaction
    @Query("SELECT * FROM documents WHERE box_id = :boxId")
    suspend fun getFullDocumentsByBoxId(boxId: Int): List<FullDocumentDbEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDocument(document: DocumentDbEntity): Long

    @Query("DELETE FROM documents WHERE id = :docId")
    suspend fun deleteDocument(docId: Int)

    @Update
    suspend fun updateDocument(document: DocumentDbEntity)

    @Query("SELECT * FROM documents WHERE id = :docId")
    suspend fun getDocumentById(docId: Int): DocumentDbEntity?

    @Query("SELECT * FROM documents WHERE box_id = :boxId")
    suspend fun getDocumentsByBoxId(boxId: Int): List<DocumentDbEntity>

    @Query("SELECT * FROM documents WHERE box_id = :boxId and barcode = :barcode")
    suspend fun getDocumentByParams(boxId: Int, barcode: String): DocumentDbEntity?

    @Query("SELECT * FROM documents WHERE barcode = :barcode")
    suspend fun getDocumentByParams(barcode: String): DocumentDbEntity?
}
