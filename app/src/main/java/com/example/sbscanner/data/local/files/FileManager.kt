package com.example.sbscanner.data.local.files

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.File
import java.io.FileOutputStream
import java.util.*

data class UrlOption(
    val baseUrl: String,
    val port: Int,
)

class FileManager(context: Context) {

    private val filesDir = context.filesDir
    private val cacheDir = context.cacheDir
    private val preferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)

    fun updateUrlOption(option: UrlOption){
        val editor = preferences.edit()
        editor.putString(KEY_URL, option.baseUrl)
        editor.putInt(KEY_PORT, option.port)
        editor.apply()
    }

    fun getUrlOption(): UrlOption {
        val url = preferences.getString(KEY_URL, DEFAULT_URL)!!
        val port = preferences.getInt(KEY_PORT, DEFAULT_PORT)
        return UrlOption(url, port)
    }

    fun saveBitmap(bitmap: Bitmap): String {
        val file = createFile(UUID.randomUUID().toString())
        if (file.exists()) file.delete()

        try {
            val out = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
            out.flush()
            out.close()
        } catch (e: Exception) {
            e.printStackTrace()
            return ""
        }
        return file.path
    }

    fun saveBitmapAsTempFileInInternalStorage(bitmap: Bitmap): String? {
        val file = createTempFile()
        if (file.exists()) file.delete()
        return try {
            val out = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)
            out.flush()
            out.close()
            file.path
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun getTempFileFromInternalStorage(path: String): File? {
        val file = File(path)
        return if (file.exists()) {
            file
        } else {
            null
        }
    }

    fun saveTempFileInInternalStorage(temp: File): String? {
        val file = createFile(UUID.randomUUID().toString())
        if (file.exists()) file.delete()
        return try {
            temp.copyTo(file)
            temp.delete()
            return file.path
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun getBitmap(path: String): Bitmap? {
        val file = File(path)
        return if (file.exists()) {
            BitmapFactory.decodeFile(path)
        } else {
            null
        }
    }

    fun deleteFile(path: String): Boolean {
        val file = File(path)
        return if (file.exists()) {
            file.delete()
        } else {
            true
        }
    }

    private fun createFile(fileName: String) = File(filesDir, fileName + JPEG_EX)

    private fun createTempFile() = File(cacheDir, TEMP_NAME + JPEG_EX)

    companion object {
        private const val JPEG_EX = ".jpg"
        private const val TEMP_NAME = "temp"
        private const val DEFAULT_URL = "https://procn.archiv.ru"
        private const val DEFAULT_PORT = 4433

        private const val SHARED_PREF_NAME = "BASE_URL_OPTION"
        private const val KEY_PORT = "KEY_PORT"
        private const val KEY_URL = "KEY_URL"
    }
}
