package com.example.sbscanner.presentation.camera2

import android.graphics.*
import android.media.Image


fun Bitmap.rotate(degrees: Float): Bitmap {
    val matrix = Matrix()
    matrix.postRotate(degrees)
    return Bitmap.createBitmap(this, 0, 0, width, height, matrix, true)
}

fun Image.toBitmap(): Bitmap {
    val buffer = this.planes[0].buffer
    val data = ByteArray(buffer.remaining()).apply { buffer.get(this) }
    return BitmapFactory.decodeByteArray(data, 0, data.size)
}

fun Bitmap.toBlackAndWhite(): Bitmap {
    val bwBitmap = Bitmap.createBitmap(this.width, this.height, Bitmap.Config.ARGB_8888)
    val canvas = Canvas(bwBitmap)
    val paint = Paint()
    val colorMatrix = ColorMatrix().apply {
        setSaturation(0f)
    }
    paint.colorFilter = ColorMatrixColorFilter(colorMatrix)
    canvas.drawBitmap(this, 0f, 0f, paint)
    return bwBitmap
}

fun Bitmap.convertToMonochrome(): Bitmap {
    val outputBitmap = Bitmap.createBitmap(this.width, this.height, Bitmap.Config.ARGB_8888)

    val threshold = 128 // Задайте ваш порог (0-255), чтобы настроить яркость пикселей

    for (x in 0 until this.width) {
        for (y in 0 until this.height) {
            val pixel = this.getPixel(x, y)
            val red = Color.red(pixel)
            val green = Color.green(pixel)
            val blue = Color.blue(pixel)

            // Вычисляем яркость пикселя как среднее арифметическое RGB-значений
            val brightness = (red + green + blue) / 3

            // Применяем бинаризацию с заданным порогом
            val newPixel = if (brightness >= threshold) Color.WHITE else Color.BLACK

            outputBitmap.setPixel(x, y, newPixel)
        }
    }

    return outputBitmap
}
