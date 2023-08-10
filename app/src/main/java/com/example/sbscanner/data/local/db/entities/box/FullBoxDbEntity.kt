package com.example.sbscanner.data.local.db.entities.box

import androidx.room.Embedded
import androidx.room.Relation
import com.example.sbscanner.data.local.db.entities.document.DocumentDbEntity
import com.example.sbscanner.data.local.db.entities.document.FullDocumentDbEntity
import com.example.sbscanner.data.local.db.entities.document.toDomain
import com.example.sbscanner.data.local.db.entities.image.toDomain
import com.example.sbscanner.domain.models.FullBox
import com.example.sbscanner.domain.models.FullDocument

data class FullBoxDbEntity(
    @Embedded val box: BoxDbEntity,
    @Relation(
        entity = DocumentDbEntity::class,
        parentColumn = "id",
        entityColumn = "box_id"
    )
    val documents: List<FullDocumentDbEntity>,
)

fun FullBoxDbEntity.toDomain() = FullBox(
    box = box.toDomain(),
    documents = documents.map { doc ->
        FullDocument(
            document = doc.document.toDomain(),
            images = doc.images.map { img -> img.toDomain() }
        )
    }
)
