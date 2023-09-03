package com.example.sbscanner.presentation.fragments.dialogs.form.document

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.viewModels
import com.example.sbscanner.databinding.FormDocumentDialogBinding
import com.example.sbscanner.domain.utils.EMPTY_ID
import com.example.sbscanner.domain.utils.isEmptyId
import com.example.sbscanner.presentation.fragments.base.BaseDialogFragment
import com.example.sbscanner.presentation.fragments.document.info.doOnChangeField
import com.example.sbscanner.presentation.fragments.document.info.getFormData
import com.example.sbscanner.presentation.fragments.document.info.setFormData
import com.example.sbscanner.presentation.utils.onBackPressed

interface FormDocListener {

    fun onCancel()

    fun onSave(docId: Int)
}

class FormDocumentDialog : BaseDialogFragment<Event, Effect, Command, State>() {

    private lateinit var binding: FormDocumentDialogBinding

    private var formListener: FormDocListener? = null

    override val viewModel: FormDocumentViewModel by viewModels { FormDocumentViewModel.Factory }

    override lateinit var initEvent: Event

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen)
        arguments?.let {
            val boxId = it.getInt(KEY_BOX, EMPTY_ID)
            val docId = it.getInt(KEY_DOC, EMPTY_ID)
            val docBarcode = it.getString(KEY_DOC_BARCODE, "")
            initEvent = if (docId.isEmptyId()) {
                Event.Ui.InitAdd(boxId, docBarcode)
            } else {
                Event.Ui.InitEdit(boxId, docId)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FormDocumentDialogBinding.inflate(inflater, container, false).apply {
            form.cancel.setOnClickListener {
                dismissWithAction { formListener?.onCancel() }
            }
            form.save.setOnClickListener {
                viewModel.commitEvent(Event.Ui.SaveDocClick(form.getFormData()))
            }
            form.doOnChangeField {
                viewModel.commitEvent(Event.Ui.ChangeForm(it))
            }

        }
        onBackPressed {
            dismissWithAction { formListener?.onCancel() }
        }
        return binding.root
    }

    fun setOnCloseListener(listener: FormDocListener) {
        formListener = listener
    }

    override fun renderState(state: State) = with(binding.form) {
        setFormData(state.formData)
    }

    override fun handleEffect(effect: Effect) {
        when (effect) {
            is Effect.CloseSaved -> {
                dismissWithAction { formListener?.onSave(effect.docId) }
            }
        }
    }

    companion object {

        private const val KEY_DOC = "KEY_DOC"

        private const val KEY_DOC_BARCODE = "KEY_DOC_BARCODE"

        private const val KEY_BOX = "KEY_BOX"

        fun newInstance(boxId: Int, docId: Int) = FormDocumentDialog().apply {
            val args = Bundle()
            args.putInt(KEY_BOX, boxId)
            args.putInt(KEY_DOC, docId)
            arguments = args
        }

        fun newInstance(boxId: Int, docBarcode: String) = FormDocumentDialog().apply {
            val args = Bundle()
            args.putInt(KEY_BOX, boxId)
            args.putString(KEY_DOC_BARCODE, docBarcode)
            arguments = args
        }
    }
}
