package com.example.sbscanner.presentation.navigation

import com.example.sbscanner.domain.utils.EMPTY_ID
import com.github.terrakok.cicerone.Router

class Presenter(
    private val router: Router
) {

    fun back() {
        router.exit()
    }

    fun onAddTaskOpen() {
        router.newRootScreen(Screens.TaskInfo(EMPTY_ID))
    }

    fun onTaskScannerOpen() {
        router.navigateTo(Screens.TaskScanner())
    }

    fun onEditTaskOpen(taskId: Int) {
        router.navigateTo(Screens.TaskInfo(taskId))
    }

    fun onSendTaskOpen(taskId: Int) {
        router.navigateTo(Screens.TaskUpload(taskId, false))
    }

    fun onProgressSendingTaskOpen(taskId: Int) {
        router.replaceScreen(Screens.BoxList(taskId))
        router.navigateTo(Screens.TaskUpload(taskId, true))
    }

    fun onBoxListOpen(taskId: Int) {
        router.replaceScreen(Screens.BoxList(taskId))
    }

    fun onBoxScannerOpen(taskId: Int) {
        router.navigateTo(Screens.BoxScanner(taskId))
    }

    fun onDocumentListOpen(boxId: Int) {
        router.replaceScreen(Screens.DocumentList(boxId))
    }

    fun onEditDocumentListOpen(boxId: Int) {
        router.navigateTo(Screens.DocumentList(boxId))
    }

    fun onAddDocumentsOpen(boxId: Int) {
        router.navigateTo(Screens.DocumentScanner(boxId))
    }

    fun onEditDocumentInfoOpen(boxId: Int, docId: Int) {
        router.navigateTo(Screens.DocumentInfoEdit(boxId, docId))
    }

    fun onAddDocumentInfoOpen(boxId: Int, docBarcode: String) {
        router.navigateTo(Screens.DocumentInfoAdd(boxId, docBarcode))
    }

    fun onAddImagesOpen(boxId: Int) {
        router.navigateTo(Screens.ImageScanner(boxId))
    }

    fun onImageListOpen(docId: Int) {
        router.navigateTo(Screens.ImageList(docId))
    }

    fun onImageInfoOpen(imgId: Int) {
        router.navigateTo(Screens.ImageInfo(imgId))
    }

    fun onOptionOpen(){
        router.navigateTo(Screens.Option())
    }
}
