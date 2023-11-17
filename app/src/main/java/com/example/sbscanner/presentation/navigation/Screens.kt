package com.example.sbscanner.presentation.navigation

import com.example.sbscanner.presentation.fragments.box.list.BoxListFragment
import com.example.sbscanner.presentation.fragments.box.scanner.BoxScannerFragment
import com.example.sbscanner.presentation.fragments.document.list.DocumentListFragment
import com.example.sbscanner.presentation.fragments.document.scanner.DocumentScannerFragment
import com.example.sbscanner.presentation.fragments.image.list.ImageListFragment
import com.example.sbscanner.presentation.fragments.image.scanner.ImageScannerFragment
import com.example.sbscanner.presentation.fragments.option.OptionFragment
import com.example.sbscanner.presentation.fragments.start.StartFragment
import com.example.sbscanner.presentation.fragments.task.info.TaskInfoFragment
import com.example.sbscanner.presentation.fragments.task.scanner.TaskScannerFragment
import com.example.sbscanner.presentation.fragments.task.upload.TaskUploadFragment
import com.example.sbscanner.presentation.fragments.test.CameraXTestFragment
import com.github.terrakok.cicerone.androidx.FragmentScreen

object Screens {

    fun Start() = FragmentScreen {
        StartFragment.newInstance()
    }

    fun TaskScanner() = FragmentScreen {
        TaskScannerFragment.newInstance()
    }

    fun TaskInfo(taskId: Int) = FragmentScreen {
        TaskInfoFragment.newInstance(taskId)
    }

    fun TaskUpload(taskId: Int, serviceIsRunning: Boolean) = FragmentScreen {
        TaskUploadFragment.newInstance(taskId, serviceIsRunning)
    }

    fun BoxList(taskId: Int) = FragmentScreen {
        BoxListFragment.newInstance(taskId)
    }

    fun BoxScanner(taskId: Int) = FragmentScreen {
        BoxScannerFragment.newInstance(taskId)
    }

    fun DocumentList(boxId: Int) = FragmentScreen {
        DocumentListFragment.newInstance(boxId)
    }

    fun DocumentScanner(boxId: Int) = FragmentScreen {
        DocumentScannerFragment.newInstance(boxId)
    }

    fun ImageScanner(boxId: Int) = FragmentScreen {
        ImageScannerFragment.newInstance(boxId)
    }

    fun ImageList(docId: Int) = FragmentScreen {
        ImageListFragment.newInstance(docId)
    }

    fun Option() = FragmentScreen {
        OptionFragment.newInstance()
    }

    fun Test() = FragmentScreen {
        CameraXTestFragment.newInstance()
    }
}
