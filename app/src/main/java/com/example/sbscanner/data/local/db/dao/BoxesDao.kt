package com.example.sbscanner.data.local.db.dao

import androidx.room.*
import com.example.sbscanner.data.local.db.entities.box.BoxDbEntity
import com.example.sbscanner.data.local.db.entities.box.BoxWithDocumentsDbEntity
import com.example.sbscanner.data.local.db.entities.box.FullBoxDbEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BoxesDao {

    @Transaction
    @Query("SELECT * FROM boxes WHERE task_id = :taskId")
    fun getFullBoxesFlowByTaskId(taskId: Int): Flow<List<FullBoxDbEntity>>

    @Transaction
    @Query("SELECT * FROM boxes WHERE task_id = :taskId")
    suspend fun getFullBoxesByTaskId(taskId: Int): List<FullBoxDbEntity>

    @Transaction
    @Query("SELECT * FROM boxes WHERE id = :boxId")
    suspend fun getFullBoxById(boxId: Int): FullBoxDbEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBox(box: BoxDbEntity): Long

    @Query("SELECT * FROM boxes WHERE task_id = :taskId and barcode = :barcode")
    suspend fun getBoxByParams(taskId: Int, barcode: String): BoxDbEntity?

    @Query("DELETE FROM boxes WHERE id = :boxId")
    suspend fun deleteBox(boxId: Int)

    @Transaction
    @Query("SELECT * FROM boxes")
    suspend fun getBoxesWithDocuments(): List<BoxWithDocumentsDbEntity>
}
