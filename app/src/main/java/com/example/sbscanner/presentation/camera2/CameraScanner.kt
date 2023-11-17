package com.example.sbscanner.presentation.camera2

import android.annotation.SuppressLint
import android.graphics.*
import android.hardware.camera2.*
import android.media.ImageReader
import android.os.Handler
import android.os.HandlerThread
import android.util.Log
import android.view.Surface
import android.view.SurfaceHolder
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import com.example.sbscanner.domain.models.Camera
import kotlinx.coroutines.*
import java.io.ByteArrayOutputStream
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine


sealed class InitCameraResult {
    object Success : InitCameraResult()
    data class Error(val msg: String?) : InitCameraResult()
}

class CameraScanner(
    private val option: CameraOption,
) : Camera {
    private lateinit var cameraDevice: CameraDevice

    private lateinit var cameraSession: CameraCaptureSession

    private var cameraLifecycle: Lifecycle? = null

    private lateinit var targets: List<Surface>

    private val scanner = Scanner(intArrayOf())

    private val cameraThread = HandlerThread("CameraThread").apply { start() }

    private val cameraHandler = Handler(cameraThread.looper)

    private val imageReaderThread = HandlerThread("ImageReaderThread").apply { start() }

    private val imageReaderHandler = Handler(imageReaderThread.looper)

    private val imageReader: ImageReader = ImageReader.newInstance(
        option.outputSize.width,
        option.outputSize.height,
        ImageFormat.JPEG,
        1
    )

    private val imageReaderAnalyzer: ImageReader = ImageReader.newInstance(
        option.outputSize.width,
        option.outputSize.height,
        ImageFormat.YUV_420_888,
        1
    )

    private val lifecycleObserver = object : DefaultLifecycleObserver {

        override fun onStop(owner: LifecycleOwner) {
            super.onStop(owner)
            Log.i("DEBUG", "CLOSE-CAMERA")
            cameraLifecycle?.removeObserver(this)
            stopCamera()
        }

        override fun onDestroy(owner: LifecycleOwner) {
            super.onDestroy(owner)
            Log.i("DEBUG", "RELEASE-CAMERA")
            releaseCameraResources()
        }
    }

    private fun stopCamera() {
        try {
            cameraDevice.close()
        } catch (exc: Throwable) {
            Log.e(TAG, "Error closing camera", exc)
        }
    }

    private fun releaseCameraResources() {
        imageReader.close()
        cameraHandler.removeCallbacksAndMessages(null)
        imageReaderHandler.removeCallbacksAndMessages(null)
        cameraThread.quitSafely()
        imageReaderThread.quitSafely()
    }

    suspend fun initializeCamera(holder: SurfaceHolder, lifecycle: Lifecycle): InitCameraResult =
        withContext(Dispatchers.Main) {
            try {
                cameraDevice = openCamera(option.cameraManager, option.cameraId, cameraHandler)
                targets = listOf(holder.surface, imageReader.surface, imageReaderAnalyzer.surface)
                cameraSession = createCaptureSession(cameraDevice, targets, cameraHandler)
                cameraLifecycle = lifecycle
                cameraLifecycle?.addObserver(lifecycleObserver)
                return@withContext InitCameraResult.Success
            } catch (e: Exception) {
                Log.e(TAG, e.message, e)
                return@withContext InitCameraResult.Error(e.message)
            }
        }

    override suspend fun startPreview() {
        val captureRequest = cameraDevice.createCaptureRequest(
            CameraDevice.TEMPLATE_PREVIEW
        ).apply {
            addTarget(targets[0])
        }
        cameraSession.setRepeatingRequest(captureRequest.build(), null, cameraHandler)
    }

    override suspend fun takePhoto(): ByteArray = suspendCancellableCoroutine { cont ->
        imageReader.setOnImageAvailableListener(ImageReader.OnImageAvailableListener { reader ->
            val image = reader.acquireLatestImage() ?: return@OnImageAvailableListener
            if (cont.isActive) {
                val bitmap = image.toBitmap().rotate(90f)
                val stream = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
                cont.resume(stream.toByteArray())
            }
            image.close()
        }, imageReaderHandler)
        val captureRequest = cameraDevice.createCaptureRequest(
            CameraDevice.TEMPLATE_STILL_CAPTURE
        ).apply {
            addTarget(imageReader.surface)
        }
        cameraSession.capture(captureRequest.build(), null, cameraHandler)
    }

    override suspend fun takePhotoString(): String {
        TODO("Not yet implemented")
    }

    override suspend fun startScanning(regex: Regex?): String =
        suspendCancellableCoroutine { cont ->
            imageReader.setOnImageAvailableListener(ImageReader.OnImageAvailableListener { reader ->
                val image = reader.acquireLatestImage() ?: return@OnImageAvailableListener
                val result = scanner.scanImage(image, regex)
                if (result !is ScannerResult.Success) {
                    image.close()
                    return@OnImageAvailableListener
                }
                if (cont.isActive) {
                    cont.resume(result.barcode)
                }
                image.close()
            }, imageReaderHandler)

            val captureRequest = cameraDevice.createCaptureRequest(
                CameraDevice.TEMPLATE_PREVIEW
            ).apply {
                addTarget(targets[0])
                addTarget(targets[1])
            }
            cameraSession.setRepeatingRequest(captureRequest.build(), null, cameraHandler)
        }

    @SuppressLint("MissingPermission")
    private suspend fun openCamera(
        manager: CameraManager,
        cameraId: String,
        handler: Handler? = null
    ): CameraDevice = suspendCancellableCoroutine { cont ->
        manager.openCamera(cameraId, object : CameraDevice.StateCallback() {
            override fun onOpened(device: CameraDevice) {
                cont.resume(device)
            }

            override fun onDisconnected(device: CameraDevice) {}

            override fun onError(device: CameraDevice, error: Int) {
                val msg = when (error) {
                    ERROR_CAMERA_DEVICE -> "Fatal (device)"
                    ERROR_CAMERA_DISABLED -> "Device policy"
                    ERROR_CAMERA_IN_USE -> "Camera in use"
                    ERROR_CAMERA_SERVICE -> "Fatal (service)"
                    ERROR_MAX_CAMERAS_IN_USE -> "Maximum cameras in use"
                    else -> "Unknown"
                }
                val exc = RuntimeException("Camera $cameraId error: ($error) $msg")
                Log.e(TAG, exc.message, exc)
                if (cont.isActive) cont.resumeWithException(exc)
            }
        }, handler)
    }

    private suspend fun createCaptureSession(
        device: CameraDevice,
        targets: List<Surface>,
        handler: Handler? = null
    ): CameraCaptureSession = suspendCoroutine { cont ->

        device.createCaptureSession(targets, object : CameraCaptureSession.StateCallback() {

            override fun onConfigured(session: CameraCaptureSession) = cont.resume(session)

            override fun onConfigureFailed(session: CameraCaptureSession) {
                val exc = RuntimeException("Camera ${device.id} session configuration failed")
                Log.e(TAG, exc.message, exc)
                cont.resumeWithException(exc)
            }
        }, handler)
    }

    companion object {
        private val TAG = Camera::class.java.simpleName
    }
}
