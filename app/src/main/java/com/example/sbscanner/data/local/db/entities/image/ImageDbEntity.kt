package com.example.sbscanner.data.local.db.entities.image

import androidx.room.*
import com.example.sbscanner.data.local.db.entities.document.DocumentDbEntity
import com.example.sbscanner.domain.models.Image

@Entity(
    tableName = "images",
    foreignKeys = [
        ForeignKey(
            entity = DocumentDbEntity::class,
            parentColumns = ["id"],
            childColumns = ["doc_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["doc_id"])
    ]
)
data class ImageDbEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    @ColumnInfo(name = "doc_id")
    val docId: Int,
    val path: String,
    val timestamp: Long,
    @ColumnInfo(name = "is_sending")
    val isSending: Boolean
)

fun ImageDbEntity.toDomain() = Image(
    id = id,
    path = path,
    timestamp = timestamp,
    isSending = isSending
)

fun Image.toLocal(docId: Int) = ImageDbEntity(
    id = id,
    docId = docId,
    path = path,
    timestamp = timestamp,
    isSending = isSending,
)
