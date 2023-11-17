package com.example.sbscanner.data.local.files

import android.content.Context
import java.io.File
import java.util.*

data class UrlOption(
    val baseUrl: String,
    val port: Int,
)

class FileManager(context: Context) {

    private val filesDir = context.filesDir
    private val preferences = context.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE)

    fun updateUrlOption(option: UrlOption) {
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

    fun getBytesFromFile(filePath: String): ByteArray? {
        val file = File(filePath)
        return if (file.exists()) {
            file.readBytes()
        } else {
            null
        }
    }

    fun getFileFromInternalStorage(path: String): File? {
        val file = File(path)
        return if (file.exists()) {
            file
        } else {
            null
        }
    }

    fun transferFileInInternalStorage(temp: File): String? {
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

    fun deleteFile(path: String): Boolean {
        val file = File(path)
        return if (file.exists()) {
            file.delete()
        } else {
            true
        }
    }

    private fun createFile(fileName: String) = File(filesDir, fileName + JPEG_EX)

    companion object {
        private const val JPEG_EX = ".jpg"
        private const val DEFAULT_URL = "http://procn.archiv.ru"
        private const val DEFAULT_PORT = 8099

        private const val SHARED_PREF_NAME = "BASE_URL_OPTION"
        private const val KEY_PORT = "KEY_PORT"
        private const val KEY_URL = "KEY_URL"
    }
}
