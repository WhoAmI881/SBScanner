package com.example.sbscanner.presentation.fragments.box.scanner

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.camera.view.PreviewView
import androidx.fragment.app.viewModels
import com.example.sbscanner.App
import com.example.sbscanner.databinding.FragmentBoxScannerBinding
import com.example.sbscanner.domain.utils.EMPTY_ID
import com.example.sbscanner.presentation.fragments.base.*
import com.example.sbscanner.presentation.fragments.test.detectedBarcodeState
import com.example.sbscanner.presentation.fragments.test.failedState
import com.example.sbscanner.presentation.fragments.test.initState
import com.example.sbscanner.presentation.fragments.test.scanningState
import com.example.sbscanner.presentation.fragments.test.setBackAction
import com.example.sbscanner.presentation.navigation.Presenter
import com.google.mlkit.vision.barcode.common.Barcode

class BoxScannerFragment : CameraXFragment<Event, Effect, Command, State>() {

    private lateinit var binding: FragmentBoxScannerBinding

    override val scanningRegex = REGEX.toRegex()

    override val scanningFormats = intArrayOf(
        Barcode.FORMAT_EAN_13
    )

    override val previewView: PreviewView
        get() = binding.camera.preview

    override val viewModel: BoxScannerViewModel by viewModels { BoxScannerViewModel.Factory }

    override lateinit var initEvent: Event

    private val presenter = Presenter(App.INSTANCE.router)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val taskId = arguments?.getInt(KEY_TASK) ?: EMPTY_ID
        initEvent = Event.Ui.Init(taskId)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentBoxScannerBinding.inflate(inflater, container, false).apply {
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
                viewModel.commitEvent(Event.Ui.BarcodeFound(event.barcode))
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

            is Effect.OpenDocumentList -> {
                presenter.onDocumentListOpen(effect.boxId)
            }
        }
    }

    companion object {

        private const val REGEX = "^[A-Za-z0-9]{7,}$"

        private const val KEY_TASK = "KEY_TASK"

        fun newInstance(taskId: Int): BoxScannerFragment {
            return BoxScannerFragment().apply {
                val args = Bundle()
                args.putInt(KEY_TASK, taskId)
                arguments = args
            }
        }
    }
}
