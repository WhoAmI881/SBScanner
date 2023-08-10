package com.example.sbscanner.presentation.adapters.base

interface DelegateItem {
    fun content(): Any
    fun id(): Int
    fun compareToOther(other: DelegateItem): Boolean
    fun changePayloads(other: DelegateItem): Boolean
}
