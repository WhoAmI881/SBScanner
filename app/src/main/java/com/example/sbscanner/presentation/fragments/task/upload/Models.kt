package com.example.sbscanner.presentation.fragments.task.upload

import com.example.sbscanner.domain.utils.EMPTY_ID

enum class FragmentState {
    INIT_UPLOAD,
    PROGRESS_SENDING,
    SUCCESS_SEND,
    LOSE_CONNECTION,
    ERROR
}

enum class ErrorType(val message: String) {
    EMPTY(""),
    USER_ID("Указан некорректный ID сотрудника!"),
    TASK_BARCODE("Ошибка авторизации листа задания!"),
    SESSION("Не удалось установить сессию!"),
    SERVER("Сервер не отвечает. Повторите попытку позже!"),
    CONNECTION("Ошибка подключения к сети!")
}

data class State(
    val taskId: Int = EMPTY_ID,
    val serviceIsRunning: Boolean = false,
    val progress: Int = 0,
    val fragmentState: FragmentState = FragmentState.INIT_UPLOAD,
    val errorType: ErrorType = ErrorType.EMPTY,
)

sealed class Event {
    sealed class Ui : Event() {
        data class Init(val taskId: Int, val serviceIsRunning: Boolean) : Ui()
        data class StartSend(val imgCount: Int) : Ui()
        data class ImageSent(val progress: Int) : Ui()
        object LoseConnection : Ui()
        object ErrorSendTask : Ui()
        object SuccessSendTask : Ui()
        object ReloadClick : Ui()
    }

    sealed class Internal : Event() {
        data class LoadedSession(val sessionId: Int) : Internal()
        data class ErrorLoadedSession(val code: Int) : Internal()
        object ErrorUserId : Internal()
        object ErrorTaskBarcode : Internal()
        data class ErrorConnection(val message: String?): Internal()
    }
}

sealed class Effect {
    data class StartUploadService(val taskId: Int, val sessionId: Int) : Effect()
    data class ShowInitTaskError(val taskId: Int, val msg: String) : Effect()
    data class ShowErrorCode(val code: Int) : Effect()
    data class ShowIOErrorMessage(val message: String): Effect()
}

sealed class Command {
    data class GetSession(val taskId: Int) : Command()
}
