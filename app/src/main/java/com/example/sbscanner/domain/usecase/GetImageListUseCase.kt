package com.example.sbscanner.domain.usecase

import com.example.sbscanner.domain.repository.ImageRepository
import kotlinx.coroutines.flow.distinctUntilChanged

class GetImageListUseCase(
    private val imageRepository: ImageRepository
) {

    operator fun invoke(docId: Int) = imageRepository.getImagesFlowByDocId(docId)
        .distinctUntilChanged()
}
