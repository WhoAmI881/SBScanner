package com.example.sbscanner.presentation.fragments.dialogs.form.image

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.signature.ObjectKey
import com.example.sbscanner.databinding.FormImageDialogBinding
import com.example.sbscanner.domain.utils.EMPTY_ID
import com.example.sbscanner.domain.utils.isEmptyId
import com.example.sbscanner.presentation.fragments.base.BaseDialogFragment
import com.example.sbscanner.presentation.utils.onBackPressed
import java.io.File

interface FormImageListener {

    fun onCancel()

    fun onSave(imgId: Int)

    fun onDelete()
}

class FormImageDialog : BaseDialogFragment<Event, Effect, Command, State>() {

    private lateinit var binding: FormImageDialogBinding

    private var formListener: FormImageListener? = null

    override val viewModel: FormImageViewModel by viewModels { FormImageViewModel.Factory }

    override lateinit var initEvent: Event

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        arguments?.let {
            val imgId = it.getInt(KEY_IMG, EMPTY_ID)
            val docId = it.getInt(KEY_DOC, EMPTY_ID)
            val imgPath = it.getString(KEY_PATH, "")
            initEvent = if (imgId.isEmptyId()) {
                Event.Ui.InitAdd(docId, imgPath)
            } else {
                Event.Ui.InitEdit(imgId)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FormImageDialogBinding.inflate(inflater, container, false).apply {
            form.cancel.setOnClickListener {
                dismissWithAction { formListener?.onCancel() }
            }
        }
        onBackPressed {
            dismissWithAction { formListener?.onCancel() }
        }
        return binding.root
    }

    fun setOnCloseListener(listener: FormImageListener) {
        formListener = listener
    }

    override fun renderState(state: State): Unit = with(binding) {
        val requestOptions = RequestOptions()
        requestOptions.signature(ObjectKey(System.currentTimeMillis()))
        Glide.with(root).load(File(state.imgPath)).apply(requestOptions).into(form.image)
        form.action.text = if (state.imgId.isEmptyId()) {
            form.action.setOnClickListener { viewModel.commitEvent(Event.Ui.SaveClick) }
            "Сохранить"
        } else {
            form.action.setOnClickListener { viewModel.commitEvent(Event.Ui.RemoveClick) }
            "Удалить"
        }
    }

    override fun handleEffect(effect: Effect) {
        when (effect) {
            is Effect.CloseDeleted -> {
                dismissWithAction { formListener?.onDelete() }
            }
            is Effect.CloseSaved -> {
                dismissWithAction { formListener?.onSave(effect.imgId) }
            }
        }
    }

    companion object {

        private const val KEY_IMG = "KEY_IMG"

        private const val KEY_DOC = "KEY_DOC"

        private const val KEY_PATH = "KEY_PATH"

        fun newInstance(imgId: Int) = FormImageDialog().apply {
            val args = Bundle()
            args.putInt(KEY_IMG, imgId)
            arguments = args
        }

        fun newInstance(docId: Int, imgPath: String) = FormImageDialog().apply {
            val args = Bundle()
            args.putInt(KEY_DOC, docId)
            args.putString(KEY_PATH, imgPath)
            arguments = args
        }
    }
}
