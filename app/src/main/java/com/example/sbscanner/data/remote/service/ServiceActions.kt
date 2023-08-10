package com.example.sbscanner.data.remote.service

enum class ServiceActions(val action: String) {
    START_SEND("START_SEND"),
    SUCCESS_SEND("SUCCESS_SEND"),
    SENT_IMAGE("SENT_IMAGE"),
    LOSE_CONNECTION("LOSE_CONNECTION"),
    SERVER_ERROR("SERVER_ERROR"),
    STOP_SEND("ACTION_STOP"),
}
