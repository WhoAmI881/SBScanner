package com.example.sbscanner.presentation.fragments.dialogs.delete.box

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.sbscanner.databinding.DialogDeleteBoxBinding
import com.example.sbscanner.domain.utils.EMPTY_ID
import com.example.sbscanner.presentation.fragments.base.BaseDialogFragment
import com.example.sbscanner.presentation.fragments.base.DialogListener

class DeleteBoxDialog : BaseDialogFragment<Event, Effect, Command, State>() {

    private lateinit var binding: DialogDeleteBoxBinding

    override val viewModel: DeleteBoxViewModel by viewModels { DeleteBoxViewModel.Factory }

    override lateinit var initEvent: Event

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val boxId = arguments?.getInt(KEY_BOX) ?: EMPTY_ID
        initEvent = Event.Ui.Init(boxId)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogDeleteBoxBinding.inflate(inflater, container, false).apply {
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

        private const val KEY_BOX = "KEY_BOX"

        fun newInstance(boxId: Int) = DeleteBoxDialog().apply {
            val args = Bundle()
            args.putInt(KEY_BOX, boxId)
            arguments = args
        }
    }
}
