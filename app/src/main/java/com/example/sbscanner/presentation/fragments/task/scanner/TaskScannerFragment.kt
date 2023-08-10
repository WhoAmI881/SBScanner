package com.example.sbscanner.presentation.fragments.task.scanner

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import com.example.sbscanner.App
import com.example.sbscanner.databinding.FragmentTaskScannerBinding
import com.example.sbscanner.presentation.fragments.base.*
import com.example.sbscanner.presentation.fragments.task.info.TaskInfoFragment
import com.example.sbscanner.presentation.navigation.Presenter

class TaskScannerFragment : CameraFragment<Event, Effect, Command, State>() {

    private lateinit var binding: FragmentTaskScannerBinding

    private val presenter = Presenter(App.INSTANCE.router)

    override val viewModel: TaskScannerViewModel by viewModels()

    override fun getSurfaceTexture(): AutoFitSurfaceView {
        return binding.camera.holder
    }

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
            is Effect.ReturnBack -> {
                setFragmentResult(
                    TaskInfoFragment.KEY_REQUEST,
                    bundleOf(TaskInfoFragment.KEY_BUNDLE to effect.barcode)
                )
                presenter.back()
            }
        }
    }

    companion object {
        fun newInstance() = TaskScannerFragment()
    }
}
