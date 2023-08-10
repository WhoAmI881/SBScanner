package com.example.sbscanner.domain.repository

import com.example.sbscanner.domain.models.Box
import com.example.sbscanner.domain.models.BoxWithDocuments
import com.example.sbscanner.domain.models.FullBox
import kotlinx.coroutines.flow.Flow

interface BoxRepository {

    fun getFullBoxesFlowByTaskId(taskId: Int): Flow<List<FullBox>>

    suspend fun getFullBoxesByTaskId(taskId: Int): List<FullBox>

    suspend fun getFullBox(boxId: Int): FullBox?

    suspend fun getBoxesByTaskId(taskId: Int): List<Box>

    suspend fun getBoxId(taskId: Int, box: Box): Int

    suspend fun addBox(taskId: Int, box: Box): Int

    suspend fun removeBox(boxId: Int)

    suspend fun getBox(boxId: Int): Box?

    suspend fun getBoxesWithDocuments(): List<BoxWithDocuments>
}
