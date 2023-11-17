package com.example.sbscanner.presentation.camera2

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageFormat
import android.graphics.Rect
import android.graphics.YuvImage
import android.media.Image
import android.util.Size
import androidx.camera.core.ImageProxy
import com.google.android.gms.tasks.Tasks
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.io.ByteArrayOutputStream

sealed class ScannerResult {
    object Failed : ScannerResult()
    object Empty : ScannerResult()
    object MultipleBarcodes : ScannerResult()
    class FormatError(val barcode: String) : ScannerResult()
    class Success(val barcode: String) : ScannerResult()
}

class Scanner(
    formats: IntArray,
    private val regex: Regex? = null,
) {

    private val scanner: BarcodeScanner

    init {
        val options = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(
                Barcode.FORMAT_CODE_128,
                *formats
            )
            .build()
        scanner = BarcodeScanning.getClient(options)
    }

    fun scanImage(image: Image, regex: Regex? = null): ScannerResult {
        return scanJpegFormat(image, regex)
    }

    fun scanImageProxy(image: ImageProxy) = try {
        val bitmap = image.toBitmap()
        val inputImage = InputImage.fromBitmap(cropBitmap(bitmap), image.imageInfo.rotationDegrees)
        val task = scanner.process(inputImage)
        val result = Tasks.await(task)
        checkResult(result, regex)
    } catch (e: Exception) {
        ScannerResult.Failed
    }

    private fun scanJpegFormat(image: Image, regex: Regex? = null) = try {
        val crop = cropBitmap(image.toBitmap())
        val task = scanner.process(crop, 90)
        val result = Tasks.await(task)
        checkResult(result, regex)
    } catch (e: Exception) {
        ScannerResult.Failed
    }

    private fun scanYuvFormat(image: Image, regex: Regex? = null) = try {
        val yuv = convertImageToYuv(image)
        val outputStream = ByteArrayOutputStream()
        yuv.compressToJpeg(cropRectImage(yuv), 100, outputStream)
        val jpegData = outputStream.toByteArray()
        val bitmap = BitmapFactory.decodeByteArray(jpegData, 0, jpegData.size)
        val task = scanner.process(bitmap, 90)
        val result = Tasks.await(task)
        checkResult(result, regex)
    } catch (e: Exception) {
        ScannerResult.Failed
    }

    private fun convertImageToYuv(image: Image): YuvImage {
        val planes = image.planes
        val yBuffer = planes[0].buffer
        val uBuffer = planes[1].buffer
        val vBuffer = planes[2].buffer
        val ySize = yBuffer.remaining()
        val uSize = uBuffer.remaining()
        val vSize = vBuffer.remaining()
        val yuvData = ByteArray(ySize + uSize + vSize)

        yBuffer.get(yuvData, 0, ySize)
        uBuffer.get(yuvData, ySize, uSize)
        vBuffer.get(yuvData, ySize + uSize, vSize)

        val width = image.width
        val height = image.height
        return YuvImage(yuvData, ImageFormat.NV21, width, height, null)
    }

    private fun cropRectImage(image: YuvImage): Rect {
        val left = image.width / 2 - DEFAULT_HEIGHT_BITMAP
        val right = image.width / 2 + DEFAULT_HEIGHT_BITMAP
        return Rect(left, 0, right, image.height)
    }

    private fun cropRectImage(image: ImageProxy): Rect {
        val left = image.width / 2 - DEFAULT_HEIGHT_BITMAP
        val right = image.width / 2 + DEFAULT_HEIGHT_BITMAP
        return Rect(left, 0, right, image.height)
    }

    private fun cropBitmap(bitmap: Bitmap): Bitmap {
        val areaSize = Size(DEFAULT_HEIGHT_BITMAP, bitmap.height)
        val startY = (bitmap.height - areaSize.height) / 2
        val startX = (bitmap.width - areaSize.width) / 2
        return Bitmap.createBitmap(bitmap, startX, startY, areaSize.width, areaSize.height)
    }

    private fun checkResult(barcodes: List<Barcode>, regex: Regex?): ScannerResult {
        if (barcodes.isEmpty()) return ScannerResult.Empty
        if (barcodes.size != 1) return ScannerResult.MultipleBarcodes
        val barcode = barcodes.first().rawValue ?: return ScannerResult.Empty
        regex?.let { if (it.matches(barcode).not()) return ScannerResult.FormatError(barcode) }
        return ScannerResult.Success(barcode)
    }

    /*
        private val scanner = MultiFormatReader()
            val hints = mutableMapOf<DecodeHintType, Any>()
        hints[DecodeHintType.POSSIBLE_FORMATS] =
            listOf(BarcodeFormat.CODE_128, BarcodeFormat.EAN_13)
        scanner.setHints(hints)
    fun scanBitmap(bitmap: Bitmap): String {
        return try {
            val result = scanner.decode(getBinaryBitmap(bitmap))
            if (result.text.isCorrectBarcode()) result.text else ""
        } catch (e: Exception) {
            ""
        }
    }

    private fun String.isCorrectBarcode(): Boolean {
        return REGEX.matches(this)
    }

    private fun getBinaryBitmap(bitmap: Bitmap): BinaryBitmap {
        val areaSize = Size(bitmap.width, DEFAULT_HEIGHT_BITMAP)
        val pixels = getPixels(bitmap, areaSize)
        val source = RGBLuminanceSource(areaSize.width, areaSize.height, pixels)
        return BinaryBitmap(HybridBinarizer(source))
    }

    private fun getPixels(bitmap: Bitmap, areaSize: Size): IntArray {
        val width = areaSize.width
        val height = areaSize.height
        val pixels = IntArray(width * height)
        val startY = (bitmap.height - height) / 2
        val startX = (bitmap.width - width) / 2
        bitmap.getPixels(pixels, 0, width, startX, startY, width, height)
        return pixels
    }

     */

    companion object {
        private const val DEFAULT_HEIGHT_BITMAP = 100
    }
}
