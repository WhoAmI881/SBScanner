package com.example.sbscanner.presentation.utils

import android.app.AlertDialog
import android.content.Context
import android.view.View
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar

fun View.showSnackbar(message: String) {
    Snackbar.make(
        this,
        message,
        Snackbar.LENGTH_SHORT
    ).show()
}

fun Context.showDialogConfirm(title: String, question: String, onConfirm: () -> Unit) {
    val alertDialog = AlertDialog.Builder(this)
        .setTitle(title)
        .setMessage(question)
        .setPositiveButton("Да") { dialog, _ ->
            dialog.dismiss()
            onConfirm()
        }
        .setNegativeButton("Отмена") { dialog, _ ->
            dialog.dismiss()
        }
        .create()
    alertDialog.show()
}

fun Context.showDialogMessage(title: String, question: String, action: () -> Unit) {
    val alertDialog = AlertDialog.Builder(this)
        .setTitle(title)
        .setMessage(question)
        .setPositiveButton("Ок") { dialog, _ ->
            dialog.dismiss()
        }.setOnDismissListener {
            action()
        }
        .create()
    alertDialog.show()
}


fun Fragment.onBackPressed(callback: () -> Unit) {
    requireActivity().onBackPressedDispatcher.addCallback(this,
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                callback()
            }
        })
}
