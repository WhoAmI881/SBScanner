package com.example.sbscanner.presentation.fragments.box.scanner

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.sbscanner.App
import com.example.sbscanner.databinding.FragmentBoxScannerBinding
import com.example.sbscanner.domain.utils.EMPTY_ID
import com.example.sbscanner.presentation.fragments.base.*
import com.example.sbscanner.presentation.navigation.Presenter

class BoxScannerFragment : CameraFragment<Event, Effect, Command, State>() {

    private lateinit var binding: FragmentBoxScannerBinding

    override val viewModel: BoxScannerViewModel by viewModels { BoxScannerViewModel.Factory }

    override lateinit var initEvent: Event

    private val presenter = Presenter(App.INSTANCE.router)

    override fun getSurfaceTexture(): AutoFitSurfaceView {
        return binding.camera.holder
    }

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

    override fun handleCameraEvent(event: CameraEvent) {
        when (event) {
            is CameraEvent.StartInit -> {
                viewModel.commitEvent(Event.Ui.ChangeCameraState(CameraState.INIT))
            }
            is CameraEvent.FailedInit -> {
                viewModel.commitEvent(Event.Ui.ChangeCameraState(CameraState.FAILED))
            }
            is CameraEvent.SuccessInit -> {
                viewModel.commitEvent(Event.Ui.CameraInit(event.cameraScanner))
            }
        }
    }

    override fun renderState(state: State) = with(binding) {
        when (state.cameraState) {
            CameraState.INIT -> camera.initState()
            CameraState.FAILED -> camera.failedState()
            CameraState.SUCCESS -> when (state.formState) {
                FormState.SCANNING -> {
                    camera.scanningState()
                }
                FormState.BARCODE_FOUND -> {
                    camera.detectedBarcodeState()
                }
                else -> {}
            }
        }
    }

    override fun handleEffect(effect: Effect) {
        when (effect) {
            is Effect.OpenDocumentList -> {
                presenter.onDocumentListOpen(effect.boxId)
            }
        }
    }

    companion object {

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
