package com.example.sbscanner.presentation.camera2

import android.graphics.ImageFormat
import android.graphics.SurfaceTexture
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.util.Size

class CameraOption(val cameraManager: CameraManager) {

    val cameraId: String

    val cameraCharacteristics: CameraCharacteristics

    val outputSize: Size

    val scanningSize: Size

    init {
        cameraId = getCameraId(cameraManager)!!
        cameraCharacteristics = cameraManager.getCameraCharacteristics(cameraId)
        outputSize = getOutputSizePreview(cameraCharacteristics)
        scanningSize = outputSize
    }

    private fun getCameraId(cameraManager: CameraManager): String? {
        return cameraManager.cameraIdList.firstOrNull {
            cameraManager.getCameraCharacteristics(it)
                .get(CameraCharacteristics.LENS_FACING) ==
                    CameraCharacteristics.LENS_FACING_BACK
        }
    }

    private fun getOutputSizePreview(cameraCharacteristics: CameraCharacteristics): Size {
        val outputSize = cameraCharacteristics
            .get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)!!
            .getOutputSizes(SurfaceTexture::class.java).filter {
                it.height >= RESOLUTION.height &&
                        it.width >= RESOLUTION.width
            }
        return outputSize.last()
    }

    private fun getScanningSize(cameraCharacteristics: CameraCharacteristics): Size {
        val outputSize = cameraCharacteristics
            .get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)!!
            .getOutputSizes(ImageFormat.JPEG)
        return outputSize.first {
            it.height < RESOLUTION.height &&
                    it.width < RESOLUTION.width
        }
    }

    companion object {
        private val RESOLUTION = Size(1280, 720)
    }
}
