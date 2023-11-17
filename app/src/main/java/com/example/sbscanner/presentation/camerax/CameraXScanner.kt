package com.example.sbscanner.presentation.camerax

import android.content.Context
import android.util.Log
import android.util.Size
import androidx.camera.core.CameraSelector
import androidx.camera.core.CameraSelector.LENS_FACING_BACK
import androidx.camera.core.CameraState
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCapture.FLASH_MODE_ON
import androidx.camera.core.ImageCapture.FLASH_MODE_OFF
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.core.Preview.SurfaceProvider
import androidx.camera.core.resolutionselector.ResolutionSelector
import androidx.camera.core.resolutionselector.ResolutionStrategy
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.concurrent.futures.await
import androidx.lifecycle.LifecycleOwner
import com.example.sbscanner.presentation.camera2.Scanner
import com.example.sbscanner.presentation.camera2.ScannerResult
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.File
import java.util.concurrent.ExecutorService
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class CameraXScanner(
    private val cameraExecutor: ExecutorService,
    private val scanner: Scanner
) {

    private var imageCapture: ImageCapture? = null

    private var imageAnalyzer: ImageAnalysis? = null

    private lateinit var outputOptions: ImageCapture.OutputFileOptions

    suspend fun initCamera(
        context: Context,
        surfaceProvider: SurfaceProvider,
        cameraLifecycleOwner: LifecycleOwner,
        cameraStateObservable: (cameraState: CameraState) -> Unit
    ) {
        val cameraProvider = ProcessCameraProvider.getInstance(context).await()

        val resolutionSelector = ResolutionSelector.Builder()
            .setResolutionStrategy(
                ResolutionStrategy(
                    RESOLUTION,
                    ResolutionStrategy.FALLBACK_RULE_CLOSEST_LOWER
                )
            ).build()

        val cameraPreview = Preview.Builder()
            .setResolutionSelector(resolutionSelector)
            .build()

        imageCapture = ImageCapture.Builder()
            .setResolutionSelector(resolutionSelector)
            .build()

        imageAnalyzer = ImageAnalysis.Builder()
            .setResolutionSelector(resolutionSelector)
            .build()

        val cameraSelector = CameraSelector.Builder()
            .requireLensFacing(LENS_FACING_BACK)
            .build()

        outputOptions = ImageCapture.OutputFileOptions
            .Builder(File(context.cacheDir, FILENAME))
            .build()

        cameraProvider.unbindAll()
        val camera = cameraProvider.bindToLifecycle(
            cameraLifecycleOwner,
            cameraSelector,
            cameraPreview,
            imageCapture,
            imageAnalyzer
        )
        camera.cameraInfo.cameraState.removeObservers(cameraLifecycleOwner)
        camera.cameraInfo.cameraState.observe(cameraLifecycleOwner) {
            cameraStateObservable(it)
        }
        cameraPreview.setSurfaceProvider(surfaceProvider)
    }

    fun startScanning(onFindBarcode: (String) -> Unit) {
        imageAnalyzer?.clearAnalyzer()
        imageAnalyzer?.setAnalyzer(cameraExecutor) { image ->
            val result = scanner.scanImageProxy(image)
            if (result is ScannerResult.Success) {
                onFindBarcode(result.barcode)
                imageAnalyzer?.clearAnalyzer()
            }
            image.close()
        }
    }

    fun stopScanning() {
        imageAnalyzer?.clearAnalyzer()
    }

    suspend fun takePhoto() = suspendCancellableCoroutine { cont ->
        imageCapture?.takePicture(
            outputOptions, cameraExecutor, object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                    cont.resumeWithException(exc)
                }

                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    cont.resume(output.savedUri)
                }
            })
    }

    fun flashModeOn(){
        imageCapture?.flashMode = FLASH_MODE_ON
    }

    fun flashModeOff(){
        imageCapture?.flashMode = FLASH_MODE_OFF
    }


    companion object {
        private const val TAG = "CameraXBasic"
        private const val FILENAME = "temp.jpeg"
        private val RESOLUTION = Size(1280, 720)
    }
}
