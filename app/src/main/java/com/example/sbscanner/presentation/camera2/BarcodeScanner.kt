package com.example.sbscanner.presentation.camera2

import android.graphics.Bitmap
import android.media.Image
import android.util.Size
import com.google.zxing.*
import com.google.zxing.common.HybridBinarizer

class BarcodeScanner {

    private val scanner = MultiFormatReader()

    init {

        val hints = mutableMapOf<DecodeHintType, Any>()
        hints[DecodeHintType.POSSIBLE_FORMATS] =
            listOf(BarcodeFormat.CODE_128, BarcodeFormat.EAN_13)
        scanner.setHints(hints)
    }


    fun scanBitmap(bitmap: Bitmap): String {
        return try {
            val result = scanner.decode(getBinaryBitmap(bitmap))
            result.text
        } catch (e: Exception) {
            ""
        }
    }

    fun scanImage(image: Image): String {
        val data = image.planes[0].buffer
        val bytes = ByteArray(data.remaining())
        data.get(bytes)

        val width = image.width
        val height = image.height
        val source = PlanarYUVLuminanceSource(bytes, width, height, 0, 0, width, height, false)

        val bitmap = BinaryBitmap(HybridBinarizer(source))

        return try {
            val result = scanner.decode(bitmap)
            result.text
        } catch (e: Exception) {
            ""
        }
    }

    private fun getBinaryBitmap(bitmap: Bitmap): BinaryBitmap {
        val areaSize = Size(bitmap.width, 100)
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
}