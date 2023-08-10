package com.example.sbscanner.presentation.adapters.boxes

import com.example.sbscanner.domain.models.FullBox

enum class BoxState {
    FULL, NOT_FULL, SENT
}

data class BoxItem(
    val id: Int,
    val barcode: String,
    val docCount: Int,
    val imgCount: Int,
    val imgSent: Int,
    val state: BoxState,
)

fun FullBox.toUi(): BoxItem {
    val imgCount = documents.sumOf { it.images.size }
    val imgSent = documents.sumOf { it.images.count { img -> img.isSending } }
    val hasEmpty = documents.any { it.images.isEmpty() }

    val state = when {
        hasEmpty || imgCount == 0 -> BoxState.NOT_FULL
        imgSent == imgCount && imgCount != 0 -> BoxState.SENT
        else -> BoxState.FULL
    }
    return BoxItem(
        id = box.id,
        barcode = box.barcode,
        docCount = documents.size,
        imgCount = imgCount,
        imgSent = imgSent,
        state = state
    )
}

fun BoxItem.toDelegate() = BoxDelegateItem(this)
