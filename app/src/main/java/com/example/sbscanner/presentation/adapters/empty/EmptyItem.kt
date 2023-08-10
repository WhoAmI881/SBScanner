package com.example.sbscanner.presentation.adapters.empty

data class EmptyItem(val content: String = "")


fun EmptyItem.toDelegate(): EmptyDelegateItem {
    return EmptyDelegateItem(this)
}
