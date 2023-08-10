package com.example.sbscanner.data.local.db.entities.box

import androidx.room.Embedded
import androidx.room.Relation
import com.example.sbscanner.data.local.db.entities.document.DocumentDbEntity
import com.example.sbscanner.data.local.db.entities.document.toDomain
import com.example.sbscanner.domain.models.BoxWithDocuments

data class BoxWithDocumentsDbEntity(
    @Embedded val box: BoxDbEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "box_id"
    )
    val documents: List<DocumentDbEntity>
)

fun BoxWithDocumentsDbEntity.toDomain() = BoxWithDocuments(
    box = box.toDomain(),
    documents = documents.map { it.toDomain() }
)
