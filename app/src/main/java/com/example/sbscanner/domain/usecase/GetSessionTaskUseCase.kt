package com.example.sbscanner.domain.usecase

import com.example.sbscanner.domain.repository.TaskRepository
import com.example.sbscanner.domain.utils.ResultWrapper

private const val ERROR_TASK_BARCODE = -1

private const val ERROR_USER_ID = -2

enum class ErrorCode(val code: Int) {
    TASK_ID(0),
    TASK_BARCODE(ERROR_TASK_BARCODE),
    USER_ID(ERROR_USER_ID),
    SESSION_ID(-3),
    SERVER(-4),
    PARSE(-5),
}

sealed class SessionResult {

    data class ErrorRequest(val error: ErrorCode) : SessionResult()

    object LostConnection : SessionResult()

    data class Success(val sessionId: Int) : SessionResult()
}

class GetSessionTaskUseCase(
    private val taskRepository: TaskRepository
) {

    suspend operator fun invoke(taskId: Int): SessionResult {
        val task = taskRepository.getTaskById(taskId)
            ?: return SessionResult.ErrorRequest(ErrorCode.TASK_ID)

        return when (val result = taskRepository.getSessionIdByTask(task)) {
            is ResultWrapper.SuccessResponse -> when (result.value) {
                ERROR_TASK_BARCODE -> {
                    SessionResult.ErrorRequest(ErrorCode.TASK_BARCODE)
                }
                ERROR_USER_ID -> {
                    SessionResult.ErrorRequest(ErrorCode.USER_ID)
                }
                else -> if (result.value >= 0) {
                    SessionResult.Success(result.value)
                } else {
                    SessionResult.ErrorRequest(ErrorCode.SESSION_ID)
                }
            }
            is ResultWrapper.ErrorResponse -> {
                SessionResult.ErrorRequest(ErrorCode.SERVER)
            }
            is ResultWrapper.ParseError -> {
                SessionResult.ErrorRequest(ErrorCode.PARSE)
            }
            is ResultWrapper.NetworkError -> {
                SessionResult.LostConnection
            }
        }
    }
}
