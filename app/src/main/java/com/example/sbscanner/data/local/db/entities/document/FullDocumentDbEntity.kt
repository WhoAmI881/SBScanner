package com.example.sbscanner.data.local.db.entities.document

import androidx.room.Embedded
import androidx.room.Relation
import com.example.sbscanner.data.local.db.entities.image.ImageDbEntity
import com.example.sbscanner.data.local.db.entities.image.toDomain
import com.example.sbscanner.domain.models.FullDocument

data class FullDocumentDbEntity(
    @Embedded val document: DocumentDbEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "doc_id"
    )
    val images: List<ImageDbEntity>
)

fun FullDocumentDbEntity.toDomain() = FullDocument(
    document.toDomain(),
    images.map { img -> img.toDomain() }
)
