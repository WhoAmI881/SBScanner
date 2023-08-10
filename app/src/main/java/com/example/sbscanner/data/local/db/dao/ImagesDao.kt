package com.example.sbscanner.data.local.db.dao

import androidx.room.*
import com.example.sbscanner.data.local.db.entities.image.ImageDbEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ImagesDao {

    @Query("DELETE FROM images WHERE id = :imageId")
    suspend fun deleteImage(imageId: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertImage(image: ImageDbEntity): Long

    @Query("SELECT * FROM images WHERE id = :imgId")
    suspend fun getImageById(imgId: Int): ImageDbEntity?

    @Query("SELECT * FROM images WHERE doc_id = :docId")
    suspend fun getImagesByDocId(docId: Int): List<ImageDbEntity>

    @Query("SELECT * FROM images WHERE doc_id = :docId")
    fun getImagesFlowByDocId(docId: Int): Flow<List<ImageDbEntity>>

    @Query("UPDATE images SET is_sending = :isSending WHERE id = :imageId")
    suspend fun updateIsSendingImage(imageId: Int, isSending: Boolean)

    @Query("SELECT * FROM images WHERE is_sending = :isSending")
    suspend fun getImagesBySendingFlag(isSending: Boolean): List<ImageDbEntity>

    @Query("SELECT * FROM images")
    fun getAllImagesFlow(): Flow<List<ImageDbEntity>>

    @Query("SELECT * FROM images")
    suspend fun getAllImages(): List<ImageDbEntity>
}
