package com.example.sbscanner.presentation.fragments.test

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.camera.view.PreviewView
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.signature.ObjectKey
import com.example.sbscanner.App
import com.example.sbscanner.R
import com.example.sbscanner.databinding.FragmentCameraXTestBinding
import com.example.sbscanner.databinding.TemplateCameraXBinding
import com.example.sbscanner.presentation.fragments.base.CameraStateType
import com.example.sbscanner.presentation.fragments.base.CameraXFragment
import com.example.sbscanner.presentation.fragments.base.CameraXEvents
import com.example.sbscanner.presentation.navigation.Presenter

fun TemplateCameraXBinding.setBackAction(backClickListener: () -> Unit) {
    backButton.setOnClickListener { backClickListener() }
}

fun TemplateCameraXBinding.initState() {
    progressBar.isVisible = true
    photoButton.isVisible = false
    barcodeRect.isVisible = false
    topInfo.text = "Подключение к камере"
}

fun TemplateCameraXBinding.failedState() {
    progressBar.isVisible = false
    photoButton.isVisible = false
    barcodeRect.isVisible = true
    barcodeRect.setBackgroundResource(R.drawable.ic_box_red)
    topInfo.text = "Подключение к камере"
}

fun TemplateCameraXBinding.scanningState() {
    progressBar.isVisible = false
    photoButton.isVisible = false
    barcodeRect.isVisible = true
    barcodeRect.setBackgroundResource(R.drawable.ic_box_orange)
    animScanner.isVisible = true
    animScanner.startAnimation(AnimationUtils.loadAnimation(root.context, R.anim.anim_scanner))
    topInfo.text = "Сканирование штрихкода"
}

fun TemplateCameraXBinding.detectedBarcodeState() {
    progressBar.isVisible = false
    photoButton.isVisible = false
    barcodeRect.isVisible = true
    barcodeRect.setBackgroundResource(R.drawable.ic_box_green)
    animScanner.isVisible = false
    animScanner.clearAnimation()
    topInfo.text = "Штрихкод найден"
}

fun TemplateCameraXBinding.takePhotoState(photoClickListener: () -> Unit) {
    progressBar.isVisible = false
    photoButton.isVisible = true
    photoButton.setOnClickListener {
        photoClickListener()
    }
    barcodeRect.isVisible = false
    topInfo.text = "Фотография"
}

class CameraXTestFragment : CameraXFragment<Event, Effect, Command, State>() {

    private lateinit var binding: FragmentCameraXTestBinding

    private val presenter = Presenter(App.INSTANCE.router)

    override val viewModel: CameraXViewModel by viewModels()

    override val initEvent = Event.Ui.Init

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCameraXTestBinding.inflate(inflater, container, false).apply {
        }
        return binding.root
    }

    override val previewView: PreviewView
        get() = binding.camera.preview

    override fun handleCameraEvent(event: CameraXEvents) {
        when (event) {
            is CameraXEvents.CameraOpening -> {
                viewModel.commitEvent(Event.Ui.CameraOpening)
            }

            is CameraXEvents.CameraOpen -> {
                viewModel.commitEvent(Event.Ui.CameraOpen)
            }

            is CameraXEvents.CameraFailed -> {
                viewModel.commitEvent(Event.Ui.CameraError)
            }

            is CameraXEvents.BarcodeFound -> {
                viewModel.commitEvent(Event.Ui.BarcodeFound(event.barcode))
            }

            is CameraXEvents.TakePhoto -> {
                viewModel.commitEvent(Event.Ui.PhotoCreated(event.uri))
            }
        }
    }

    override fun renderState(state: State): Unit = with(binding) {
        when (state.cameraStateType) {
            CameraStateType.INIT -> {
                camera.initState()
            }

            CameraStateType.FAILED -> {
                camera.failedState()
            }

            CameraStateType.OPEN -> when (state.formStateType) {
                TestFormState.SCANNING -> {
                    img.isVisible = false
                    camera.root.isVisible = true
                    camera.scanningState()
                }

                TestFormState.BARCODE_FOUND -> {
                    camera.detectedBarcodeState()
                }

                TestFormState.TAKE_PHOTO -> {
                }

                TestFormState.PHOTO -> {
                    img.isVisible = true
                    camera.root.isVisible = false
                    val requestOptions = RequestOptions()
                    requestOptions.signature(ObjectKey(System.currentTimeMillis()))
                    Glide.with(root).load(state.imgUri).apply(requestOptions).into(img)
                }
            }
        }
    }

    override fun handleEffect(effect: Effect) {
        when (effect) {
            is Effect.StartScanning -> {
                super.startScanning()
            }

            is Effect.TakePhoto -> {
                super.takePhoto()
            }
        }
    }

    companion object {
        fun newInstance() = CameraXTestFragment()
    }
}
