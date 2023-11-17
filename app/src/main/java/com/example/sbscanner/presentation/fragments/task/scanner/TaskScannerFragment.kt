package com.example.sbscanner.presentation.fragments.task.scanner

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.camera.view.PreviewView
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import com.example.sbscanner.App
import com.example.sbscanner.databinding.FragmentTaskScannerBinding
import com.example.sbscanner.presentation.fragments.base.*
import com.example.sbscanner.presentation.fragments.task.info.TaskInfoFragment
import com.example.sbscanner.presentation.fragments.test.detectedBarcodeState
import com.example.sbscanner.presentation.fragments.test.failedState
import com.example.sbscanner.presentation.fragments.test.initState
import com.example.sbscanner.presentation.fragments.test.scanningState
import com.example.sbscanner.presentation.fragments.test.setBackAction
import com.example.sbscanner.presentation.navigation.Presenter
import com.google.mlkit.vision.barcode.common.Barcode

class TaskScannerFragment : CameraXFragment<Event, Effect, Command, State>() {

    private lateinit var binding: FragmentTaskScannerBinding

    private val presenter = Presenter(App.INSTANCE.router)

    override val previewView: PreviewView
        get() = binding.camera.preview

    override val scanningFormats = intArrayOf(
        Barcode.FORMAT_CODE_128,
        Barcode.FORMAT_EAN_13,
        Barcode.FORMAT_CODE_39
    )

    override val viewModel: TaskScannerViewModel by viewModels { TaskScannerViewModel.Factory }

    override val initEvent = Event.Ui.Init

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTaskScannerBinding.inflate(inflater, container, false).apply {
            camera.setBackAction { presenter.back() }
        }
        return binding.root
    }

    override fun handleCameraEvent(event: CameraXEvents) {
        when (event) {
            is CameraXEvents.CameraOpening -> {
                viewModel.commitEvent(Event.Ui.ChangeCameraState(CameraStateType.INIT))
            }

            is CameraXEvents.CameraFailed -> {
                viewModel.commitEvent(Event.Ui.ChangeCameraState(CameraStateType.FAILED))
            }

            is CameraXEvents.CameraOpen -> {
                viewModel.commitEvent(Event.Ui.ChangeCameraState(CameraStateType.OPEN))
            }

            is CameraXEvents.BarcodeFound -> {
                setFragmentResult(
                    TaskInfoFragment.KEY_REQUEST,
                    bundleOf(TaskInfoFragment.KEY_BUNDLE to event.barcode)
                )
                presenter.back()
            }

            else -> {}
        }
    }

    override fun renderState(state: State) = with(binding) {
        when (state.cameraStateType) {
            CameraStateType.INIT -> camera.initState()
            CameraStateType.FAILED -> camera.failedState()
            CameraStateType.OPEN -> when (state.formStateType) {
                FormStateType.SCANNING -> {
                    camera.scanningState()
                }

                FormStateType.BARCODE_FOUND -> {
                    camera.detectedBarcodeState()
                }

                else -> {}
            }
        }
    }

    override fun handleEffect(effect: Effect) {
        when (effect) {
            is Effect.StartScanning -> {
                super.startScanning()
            }
        }
    }

    companion object {
        fun newInstance() = TaskScannerFragment()
    }
}
