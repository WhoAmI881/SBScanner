package com.example.sbscanner.data.local.db.dao

import androidx.room.*
import com.example.sbscanner.data.local.db.entities.task.TaskDbEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TasksDao {

    @Update
    suspend fun updateTask(task: TaskDbEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: TaskDbEntity): Long

    @Query("DELETE FROM tasks WHERE id = :taskId")
    suspend fun deleteTask(taskId: Int)

    @Query("SELECT * FROM tasks WHERE id = :taskId")
    fun getTaskFlowById(taskId: Int): Flow<TaskDbEntity?>

    @Query("SELECT * FROM tasks WHERE id = :taskId")
    suspend fun getTaskById(taskId: Int): TaskDbEntity?

    @Query("SELECT * FROM tasks")
    suspend fun getAllTasks(): List<TaskDbEntity>

    @Query("SELECT * FROM tasks WHERE user_id = :userId and barcode = :barcode")
    suspend fun getTaskByParams(userId: String, barcode: String): TaskDbEntity?
}
