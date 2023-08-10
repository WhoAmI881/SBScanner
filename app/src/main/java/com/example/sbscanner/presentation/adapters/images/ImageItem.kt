package com.example.sbscanner.presentation.adapters.images

import com.example.sbscanner.domain.models.Image
import java.io.File

data class ImageItem(
    val id: Int,
    val file: File
)

fun Image.toUi() = ImageItem(
    id = id,
    file = File(path)
)

fun ImageItem.toDelegate() = ImageDelegateItem(this)
