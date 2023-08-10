package com.example.sbscanner.data.local.db.entities.box

import androidx.room.*
import com.example.sbscanner.data.local.db.entities.task.TaskDbEntity
import com.example.sbscanner.domain.models.Box

@Entity(
    tableName = "boxes",
    foreignKeys = [
        ForeignKey(
            entity = TaskDbEntity::class,
            parentColumns = ["id"],
            childColumns = ["task_id"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["task_id"])
    ]
)
data class BoxDbEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    @ColumnInfo(name = "task_id")
    val taskId: Int,
    val barcode: String,
    val timestamp: Long
)

fun BoxDbEntity.toDomain() = Box(
    id = id,
    barcode = barcode,
    timestamp = timestamp
)

fun Box.toLocal(taskId: Int) = BoxDbEntity(
    id = id,
    taskId = taskId,
    barcode = barcode,
    timestamp = timestamp
)
