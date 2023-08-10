package com.example.sbscanner.presentation.fragments.dialogs.delete.document

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.sbscanner.databinding.DialogDeleteDocumentBinding
import com.example.sbscanner.domain.utils.EMPTY_ID
import com.example.sbscanner.presentation.fragments.base.BaseDialogFragment
import com.example.sbscanner.presentation.fragments.base.DialogListener

class DeleteDocumentDialog : BaseDialogFragment<Event, Effect, Command, State>() {

    private lateinit var binding: DialogDeleteDocumentBinding

    override val viewModel: DeleteDocumentViewModel by viewModels { DeleteDocumentViewModel.Factory }

    override lateinit var initEvent: Event

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val docId = arguments?.getInt(KEY_DOC) ?: EMPTY_ID
        initEvent = Event.Ui.Init(docId)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogDeleteDocumentBinding.inflate(inflater, container, false).apply {
            dialog.cancel.setOnClickListener { dismiss() }
        }
        return binding.root
    }

    fun setOnDialogListener(dialogListener: DialogListener) {
        this.dialogListener = dialogListener
    }

    override fun renderState(state: State) = with(binding) {
        dialog.progressBar.progress = state.progress
        dialog.progressInfo.text = "Удалено: ${state.progress}%"
    }

    override fun handleEffect(effect: Effect) {
        when (effect) {
            is Effect.CloseDialog -> {
                dismiss()
            }
        }
    }

    companion object {

        private const val KEY_DOC = "KEY_DOC"

        fun newInstance(docId: Int) = DeleteDocumentDialog().apply {
            val args = Bundle()
            args.putInt(KEY_DOC, docId)
            arguments = args
        }
    }
}
