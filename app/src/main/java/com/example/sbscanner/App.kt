package com.example.sbscanner

import android.app.Application
import com.example.sbscanner.data.local.db.AppDatabase
import com.example.sbscanner.data.local.files.FileManager
import com.example.sbscanner.data.local.source.BoxLocalDataSourceImpl
import com.example.sbscanner.data.local.source.DocumentLocalDataSourceImpl
import com.example.sbscanner.data.local.source.ImageLocalDataSourceImpl
import com.example.sbscanner.data.local.source.TaskLocalDataSourceImpl
import com.example.sbscanner.data.remote.retrofit.RetrofitModule
import com.example.sbscanner.data.remote.source.BoxRemoteDataSourceImpl
import com.example.sbscanner.data.remote.source.DocumentRemoteDataSourceImpl
import com.example.sbscanner.data.remote.source.ImageRemoteDataSourceImpl
import com.example.sbscanner.data.remote.source.TaskRemoteDataSourceImpl
import com.example.sbscanner.data.repository.BoxRepositoryImpl
import com.example.sbscanner.data.repository.DocumentRepositoryImpl
import com.example.sbscanner.data.repository.ImageRepositoryImpl
import com.example.sbscanner.data.repository.TaskRepositoryImpl
import com.example.sbscanner.domain.usecase.*
import com.github.terrakok.cicerone.Cicerone

class App : Application() {

    private val cicerone = Cicerone.create()
    val router get() = cicerone.router
    val navigatorHolder get() = cicerone.getNavigatorHolder()

    private lateinit var database: AppDatabase
    private lateinit var retrofit: RetrofitModule
    lateinit var fileManager: FileManager

    private lateinit var taskRepository: TaskRepositoryImpl
    private lateinit var boxRepository: BoxRepositoryImpl
    private lateinit var documentRepository: DocumentRepositoryImpl
    private lateinit var imageRepository: ImageRepositoryImpl

    val testUseCase by lazy {
        TestUseCase(
            taskRepository,
            boxRepository,
            documentRepository,
            imageRepository
        )
    }

    val getTaskListUseCase by lazy { GetTaskListUseCase(taskRepository) }
    val saveTaskUseCase by lazy { SaveTaskUseCase(taskRepository) }
    val getTaskUseCase by lazy { GetTaskUseCase(taskRepository) }
    val removeTaskUseCase by lazy {
        RemoveTaskUseCase(
            taskRepository,
            boxRepository,
            documentRepository,
            imageRepository
        )
    }
    val sendTaskUseCase by lazy {
        SendTaskUseCase(
            boxRepository,
            imageRepository
        )
    }
    val getSessionTaskUseCase by lazy { GetSessionTaskUseCase(taskRepository) }

    val getFullBoxListUseCase by lazy { GetFullBoxListUseCase(boxRepository) }
    val saveBoxUseCase by lazy { SaveBoxUseCase(boxRepository) }
    val removeBoxUseCase by lazy {
        RemoveBoxUseCase(
            boxRepository,
            documentRepository,
            imageRepository
        )
    }
    val getBoxUseCase by lazy { GetBoxUseCase(boxRepository) }

    val getDocumentUseCase by lazy { GetDocumentUseCase(documentRepository) }
    val getFullDocumentListUseCase by lazy { GetFullDocumentListUseCase(documentRepository) }
    val removeDocumentUseCase by lazy { RemoveDocumentUseCase(documentRepository, imageRepository) }
    val saveDocumentUseCase by lazy { SaveDocumentUseCase(documentRepository) }
    val scanningDocumentUseCase by lazy { ScanningDocumentUseCase(boxRepository) }

    val saveImageUseCase by lazy { SaveImageUseCase(imageRepository) }
    val getImageListInDocumentUseCase by lazy { GetImageListInDocumentUseCase(imageRepository) }
    val getImageUseCase by lazy { GetImageUseCase(imageRepository) }
    val removeImageUseCase by lazy { RemoveImageUseCase(imageRepository) }
    val takePhotoUseCase by lazy { TakePhotoUseCase(imageRepository) }

    override fun onCreate() {
        super.onCreate()

        database = AppDatabase.getInstance(baseContext)
        fileManager = FileManager(baseContext)
        retrofit = RetrofitModule(fileManager.getUrlOption())

        val taskLocalDataSource = TaskLocalDataSourceImpl(database.getTasksDao())
        val taskRemoteDataSource = TaskRemoteDataSourceImpl(retrofit.taskApi)
        taskRepository = TaskRepositoryImpl(taskLocalDataSource, taskRemoteDataSource)

        val boxLocalDataSource = BoxLocalDataSourceImpl(database.getBoxesDao())
        val boxRemoteDataSource = BoxRemoteDataSourceImpl(retrofit.boxApi)
        boxRepository = BoxRepositoryImpl(boxLocalDataSource, boxRemoteDataSource)

        val documentLocalDataSource = DocumentLocalDataSourceImpl(database.getDocumentsDao())
        val documentRemoteDataSource = DocumentRemoteDataSourceImpl(retrofit.documentApi)
        documentRepository =
            DocumentRepositoryImpl(documentLocalDataSource, documentRemoteDataSource)

        val imageLocalDataSource = ImageLocalDataSourceImpl(database.getImagesDao(), fileManager)
        val imageRemoteDataSource = ImageRemoteDataSourceImpl(retrofit.imageApi)
        imageRepository = ImageRepositoryImpl(imageLocalDataSource, imageRemoteDataSource)

        INSTANCE = this
    }

    companion object {
        internal lateinit var INSTANCE: App
            private set
    }
}
