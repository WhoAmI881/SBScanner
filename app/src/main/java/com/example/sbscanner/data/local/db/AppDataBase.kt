package com.example.sbscanner.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.sbscanner.data.local.db.dao.BoxesDao
import com.example.sbscanner.data.local.db.dao.DocumentsDao
import com.example.sbscanner.data.local.db.dao.ImagesDao
import com.example.sbscanner.data.local.db.dao.TasksDao
import com.example.sbscanner.data.local.db.entities.box.BoxDbEntity
import com.example.sbscanner.data.local.db.entities.document.DocumentDbEntity
import com.example.sbscanner.data.local.db.entities.image.ImageDbEntity
import com.example.sbscanner.data.local.db.entities.task.TaskDbEntity

@Database(
    version = 1,
    entities = [
        TaskDbEntity::class,
        BoxDbEntity::class,
        DocumentDbEntity::class,
        ImageDbEntity::class
    ]
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun getTasksDao(): TasksDao

    abstract fun getBoxesDao(): BoxesDao

    abstract fun getDocumentsDao(): DocumentsDao

    abstract fun getImagesDao(): ImagesDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "database.db"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}
