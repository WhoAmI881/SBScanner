package com.example.sbscanner.presentation.fragments.image.info

import android.graphics.Bitmap
import androidx.core.view.isVisible
import com.example.sbscanner.databinding.TemplateImageFormBinding

fun TemplateImageFormBinding.show(bitmap: Bitmap?){
    root.isVisible = true
    bitmap?.let { image.setImageBitmap(it) }
}

fun TemplateImageFormBinding.close(){
    root.isVisible = false
}