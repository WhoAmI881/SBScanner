package com.example.sbscanner.presentation.camera2

import android.graphics.Bitmap
import android.util.Size
import com.google.android.gms.tasks.Tasks
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode

sealed class ScannerResult {
    object Failed : ScannerResult()
    object Empty : ScannerResult()
    object MultipleBarcodes : ScannerResult()
    class FormatError(val barcode: String) : ScannerResult()
    class Success(val barcode: String) : ScannerResult()
}

class Scanner {

    private val scanner: BarcodeScanner

    init {
        val options = BarcodeScannerOptions.Builder()
            .setBarcodeFormats(
                Barcode.FORMAT_CODE_128,
                Barcode.FORMAT_EAN_13
            )
            .build()
        scanner = BarcodeScanning.getClient(options)
    }

    fun scanBitmap(bitmap: Bitmap, regex: Regex? = null) = try {
        val crop = cropBitmap(bitmap)
        val task = scanner.process(crop, 90)
        val result = Tasks.await(task)
        checkResult(result, regex)
    } catch (e: Exception) {
        ScannerResult.Failed
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
