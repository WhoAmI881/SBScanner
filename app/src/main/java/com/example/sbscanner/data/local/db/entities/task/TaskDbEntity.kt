package com.example.sbscanner.data.local.db.entities.task

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.sbscanner.domain.models.Task

@Entity(
    tableName = "tasks"
)
data class TaskDbEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    @ColumnInfo(name = "user_id")
    val userId: String,
    val barcode: String,
    val timestamp: Long,
)

fun TaskDbEntity.toDomain() = Task(
    id = id,
    userId = userId,
    barcode = barcode,
    timestamp = timestamp
)

fun Task.toLocal() = TaskDbEntity(
    id = id,
    userId = userId,
    barcode = barcode,
    timestamp = timestamp
)
