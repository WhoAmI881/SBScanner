package com.example.sbscanner.data.local.db.entities.document

import androidx.room.*
import com.example.sbscanner.data.local.db.entities.box.BoxDbEntity
import com.example.sbscanner.domain.models.Document

@Entity(
    tableName = "documents",
    foreignKeys = [
        ForeignKey(
            entity = BoxDbEntity::class,
            parentColumns = ["id"],
            childColumns = ["box_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["box_id"])
    ]
)
data class DocumentDbEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    @ColumnInfo(name = "box_id")
    val boxId: Int,
    val barcode: String,
    val title: String,
    val date: String,
    @ColumnInfo(name = "is_simple_inventory")
    val note: String,
    val isSimpleInventory: Boolean,
    val timestamp: Long
)

fun DocumentDbEntity.toDomain() = Document(
    id = id,
    barcode = barcode,
    title = title,
    isSimpleInventory = isSimpleInventory,
    date = date,
    note = note,
    timestamp = timestamp
)

fun Document.toLocal(boxId: Int) = DocumentDbEntity(
    id = id,
    boxId = boxId,
    barcode = barcode,
    title = title,
    isSimpleInventory = isSimpleInventory,
    date = date,
    note = note,
    timestamp = timestamp
)
