package com.example.sbscanner.presentation.fragments.base

import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.camera.core.CameraState
import androidx.camera.view.PreviewView
import androidx.lifecycle.lifecycleScope
import com.example.sbscanner.presentation.camera2.Scanner
import com.example.sbscanner.presentation.camerax.CameraXScanner
import kotlinx.coroutines.launch
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

sealed class CameraXEvents {
    object CameraOpening : CameraXEvents()
    object CameraOpen : CameraXEvents()
    object CameraFailed : CameraXEvents()
    class BarcodeFound(val barcode: String) : CameraXEvents()
    class TakePhoto(val uri: Uri?) : CameraXEvents()
}

abstract class CameraXFragment<Event : Any, Effect : Any, Command : Any, State : Any> :
    BaseFragment<Event, Effect, Command, State>() {

    private lateinit var cameraExecutor: ExecutorService

    private lateinit var cameraXScanner: CameraXScanner

    private lateinit var scanner: Scanner

    private var _flashIsOn: Boolean = false

    val flashIsOn: Boolean
        get() = _flashIsOn

    abstract val previewView: PreviewView

    abstract fun handleCameraEvent(event: CameraXEvents)

    open val scanningRegex: Regex? = null

    open val scanningFormats = intArrayOf()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        cameraExecutor = Executors.newSingleThreadExecutor()
        scanner = Scanner(scanningFormats, scanningRegex)
        cameraXScanner = CameraXScanner(cameraExecutor, scanner)
        previewView.post { initCamera() }
    }

    fun startScanning() {
        cameraXScanner.startScanning {
            handleCameraEvent(CameraXEvents.BarcodeFound(it))
        }
    }

    fun stopScanning() {
        cameraXScanner.stopScanning()
    }

    fun takePhoto() {
        viewLifecycleOwner.lifecycleScope.launch {
            try{
                cameraXScanner.takePhoto()?.let {
                    handleCameraEvent(CameraXEvents.TakePhoto(it))
                }
            }catch (e: Exception){
                Log.e("failed capture", e.toString())
            }
        }
    }

    fun flashOn() {
        _flashIsOn = true
        cameraXScanner.flashModeOn()
    }

    fun flashOff() {
        _flashIsOn = false
        cameraXScanner.flashModeOff()
    }

    private fun initCamera() {
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                cameraXScanner.initCamera(
                    requireContext(),
                    previewView.surfaceProvider,
                    viewLifecycleOwner
                ) {
                    when (it.type) {
                        CameraState.Type.PENDING_OPEN -> {
                            Log.e("DEBUG", "CameraState: Pending Open")
                        }

                        CameraState.Type.OPENING -> {
                            Log.e("DEBUG", "CameraState: Opening")
                            handleCameraEvent(CameraXEvents.CameraOpening)
                        }

                        CameraState.Type.OPEN -> {
                            Log.e("DEBUG", "CameraState: Open")
                            handleCameraEvent(CameraXEvents.CameraOpen)
                        }

                        CameraState.Type.CLOSING -> {
                            Log.e("DEBUG", "CameraState: Closing")
                        }

                        CameraState.Type.CLOSED -> {
                            Log.e("DEBUG", "CameraState: Closed")
                        }
                    }
                    it.error?.let { error ->
                        Log.e("DEBUG", "CameraState: Error $error")
                        handleCameraEvent(CameraXEvents.CameraFailed)
                    }
                }
            } catch (e: Exception) {
                handleCameraEvent(CameraXEvents.CameraFailed)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        cameraExecutor.shutdown()
    }
}
