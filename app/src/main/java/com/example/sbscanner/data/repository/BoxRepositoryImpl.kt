package com.example.sbscanner.data.repository

import com.example.sbscanner.data.source.local.BoxLocalDataSource
import com.example.sbscanner.data.source.remote.BoxRemoteDataSource
import com.example.sbscanner.domain.models.Box
import com.example.sbscanner.domain.models.BoxWithDocuments
import com.example.sbscanner.domain.models.FullBox
import com.example.sbscanner.domain.repository.BoxRepository
import kotlinx.coroutines.flow.Flow

class BoxRepositoryImpl(
    private val boxLocalDataSource: BoxLocalDataSource,
    private val boxRemoteDataSource: BoxRemoteDataSource
) : BoxRepository {

    override fun getFullBoxesFlowByTaskId(taskId: Int): Flow<List<FullBox>> {
        return boxLocalDataSource.getFullBoxesFlowByTaskId(taskId)
    }

    override suspend fun getFullBoxesByTaskId(taskId: Int): List<FullBox> {
        return boxLocalDataSource.getFullBoxesByTaskId(taskId)
    }

    override suspend fun getFullBox(boxId: Int): FullBox? {
        return boxLocalDataSource.getFullBox(boxId)
    }

    override suspend fun getBoxesByTaskId(taskId: Int): List<Box> {
        return boxLocalDataSource.getBoxesByTaskId(taskId)
    }

    override suspend fun getBoxId(taskId: Int, box: Box): Int {
        return boxLocalDataSource.getBoxId(taskId, box)
    }

    override suspend fun addBox(taskId: Int, box: Box): Int {
        return boxLocalDataSource.addBox(taskId, box)
    }

    override suspend fun removeBox(boxId: Int) {
        boxLocalDataSource.removeBox(boxId)
    }

    override suspend fun getBox(boxId: Int): Box? {
        return boxLocalDataSource.getBox(boxId)
    }

    override suspend fun getBoxesWithDocuments(): List<BoxWithDocuments> {
        return boxLocalDataSource.getBoxesWithDocuments()
    }
}
