package com.example.sbscanner.data.local.source

import com.example.sbscanner.data.local.db.dao.BoxesDao
import com.example.sbscanner.data.local.db.entities.box.toDomain
import com.example.sbscanner.data.local.db.entities.box.toLocal
import com.example.sbscanner.data.source.local.BoxLocalDataSource
import com.example.sbscanner.domain.models.Box
import com.example.sbscanner.domain.models.BoxWithDocuments
import com.example.sbscanner.domain.models.FullBox
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class BoxLocalDataSourceImpl(
    private val boxesDao: BoxesDao
) : BoxLocalDataSource {

    override fun getFullBoxesFlowByTaskId(taskId: Int): Flow<List<FullBox>> {
        return boxesDao.getFullBoxesFlowByTaskId(taskId).map { item -> item.map { it.toDomain() } }
    }

    override suspend fun getFullBoxesByTaskId(taskId: Int): List<FullBox> {
        return boxesDao.getFullBoxesByTaskId(taskId).map { it.toDomain() }
    }

    override suspend fun getFullBox(boxId: Int): FullBox? {
        return boxesDao.getFullBoxById(boxId)?.toDomain()
    }

    override suspend fun getBoxId(taskId: Int, box: Box): Int {
        return boxesDao.getBoxByParams(taskId, box.barcode)?.id ?: 0
    }

    override suspend fun addBox(taskId: Int, box: Box): Int {
        return boxesDao.insertBox(box.toLocal(taskId)).toInt()
    }

    override suspend fun removeBox(boxId: Int) {
        boxesDao.deleteBox(boxId)
    }

    override suspend fun getBoxesWithDocuments(): List<BoxWithDocuments> {
        return boxesDao.getBoxesWithDocuments().map { it.toDomain() }
    }
}
