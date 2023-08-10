package com.example.sbscanner.presentation.fragments.base

import android.content.Context
import android.hardware.camera2.CameraManager
import android.os.Bundle
import android.util.Log
import android.view.SurfaceHolder
import android.view.View
import android.view.animation.AnimationUtils
import androidx.core.view.isVisible
import androidx.lifecycle.*
import com.example.sbscanner.R
import com.example.sbscanner.databinding.TemplateCameraBinding
import com.example.sbscanner.presentation.camera2.CameraOption
import com.example.sbscanner.presentation.camera2.CameraScanner
import com.example.sbscanner.presentation.camera2.InitCameraResult
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch

sealed class CameraEvent {
    object StartInit : CameraEvent()
    data class SuccessInit(val cameraScanner: CameraScanner) : CameraEvent()
    object FailedInit : CameraEvent()
}

enum class CameraState {
    INIT, FAILED, SUCCESS
}

enum class FormState {
    SCANNING, BARCODE_FOUND, TAKE_PHOTO
}

fun TemplateCameraBinding.setBackAction(action: () -> Unit) {
    back.setOnClickListener { action() }
}

fun TemplateCameraBinding.initState() {
    progressBar.isVisible = true
    action.isVisible = false
    barcode.isVisible = false
    info.text = "Подключение к камере"
}

fun TemplateCameraBinding.failedState() {
    barcode.setBackgroundResource(R.drawable.ic_box_red)
    barcode.isVisible = true
    progressBar.isVisible = false
    info.text = "Не удалось подключиться к камере"
}

fun TemplateCameraBinding.scanningState() {
    barcode.setBackgroundResource(R.drawable.ic_box_orange)
    animScanner.startAnimation(AnimationUtils.loadAnimation(root.context, R.anim.anim_scanner))
    animScanner.isVisible = true
    barcode.isVisible = true
    action.isVisible = false
    progressBar.isVisible = false
    info.text = "Сканирование штрихкода"
}

fun TemplateCameraBinding.detectedBarcodeState() {
    barcode.setBackgroundResource(R.drawable.ic_box_green)
    animScanner.clearAnimation()
    barcode.isVisible = true
    animScanner.isVisible = false
    info.text = "Штрихкод найден"
}

fun TemplateCameraBinding.takePhotoState(actionClick: () -> Unit) {
    action.isVisible = true
    barcode.isVisible = false
    progressBar.isVisible = false
    action.text = "Фото"
    action.setOnClickListener { actionClick() }
    info.text = "Фотография"
}

fun TemplateCameraBinding.processingPhotoState(actionClick: () -> Unit) {
    action.isVisible = true
    barcode.isVisible = false
    progressBar.isVisible = false
    action.text = "Фото"
    action.setOnClickListener { actionClick() }
    info.text = "Фотография"
}

fun TemplateCameraBinding.stopScanning() {
    barcode.setBackgroundResource(R.drawable.ic_box_red)
    animScanner.clearAnimation()
    barcode.isVisible = true
    action.isVisible = true
    animScanner.isVisible = false
    action.text = "Начать"
}

abstract class CameraFragment<Event : Any, Effect : Any, Command : Any, State : Any> :
    BaseFragment<Event, Effect, Command, State>() {

    private val cameraEvents: MutableSharedFlow<CameraEvent> = MutableSharedFlow()

    abstract fun getSurfaceTexture(): AutoFitSurfaceView

    abstract fun handleCameraEvent(event: CameraEvent)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.i("DEBUG", "ViewCreated")
        with(viewLifecycleOwner.lifecycleScope) {
            launch {
                repeatOnLifecycle(Lifecycle.State.RESUMED) {
                    cameraEvents.collect { handleCameraEvent(it) }
                }
            }
        }
        getSurfaceTexture().holder.addCallback(object : SurfaceHolder.Callback {
            override fun surfaceDestroyed(holder: SurfaceHolder) {
                Log.i("DEBUG", "surfaceDestroyed")
            }

            override fun surfaceChanged(
                holder: SurfaceHolder,
                format: Int,
                width: Int,
                height: Int
            ) = Unit

            override fun surfaceCreated(holder: SurfaceHolder) {
                Log.i("DEBUG", "surfaceCreated")
                initCamera()
            }
        })
    }

    private fun initCamera() {
        val cameraManager =
            requireContext().getSystemService(Context.CAMERA_SERVICE) as CameraManager
        val cameraOption = CameraOption(cameraManager)
        val cameraScanner = CameraScanner(cameraOption)
        getSurfaceTexture().setAspectRatio(
            cameraOption.outputSize.width,
            cameraOption.outputSize.height,
        )
        viewLifecycleOwner.lifecycleScope.launch {
            cameraEvents.emit(CameraEvent.StartInit)
            when (cameraScanner.initializeCamera(
                getSurfaceTexture().holder,
                viewLifecycleOwner.lifecycle
            )) {
                is InitCameraResult.Success -> {
                    cameraEvents.emit(CameraEvent.SuccessInit(cameraScanner))
                }
                is InitCameraResult.Error -> {
                    cameraEvents.emit(CameraEvent.FailedInit)
                }
            }
        }
    }
}

/*
abstract val preview: TextureView

private val surfaceTextureListener = object : TextureView.SurfaceTextureListener {
    override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
        adaptabilityCameraPreview(cameraOption.outputSize, preview)
        initCamera()
    }

    override fun onSurfaceTextureSizeChanged(
        surface: SurfaceTexture,
        width: Int,
        height: Int
    ) {
    }

    override fun onSurfaceTextureDestroyed(surface: SurfaceTexture) = false
    override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {}
}

    private fun adaptabilityCameraPreview(surfaceSize: Size, preview: TextureView) {
    val scaleFactors = if (preview.height <= preview.width) {
        val previewRatio = surfaceSize.width / surfaceSize.height.toFloat()
        val viewFinderRatio = preview.width / preview.height.toFloat()
        val scaling = viewFinderRatio * previewRatio
        PointF(1f, scaling)
    } else {
        val previewRatio = surfaceSize.height / surfaceSize.width.toFloat()
        val viewFinderRatio = preview.height / preview.width.toFloat()
        val scaling = viewFinderRatio * previewRatio
        PointF(scaling, 1f)
    }

    val matrix = Matrix()
    matrix.preScale(
        scaleFactors.x, scaleFactors.y,
        preview.width / 2f, preview.height / 2f
    )
    preview.setTransform(matrix)

        if (preview.isAvailable) {
            initCamera()
        } else {
            preview.surfaceTextureListener = surfaceTextureListener
        }
}
 */
