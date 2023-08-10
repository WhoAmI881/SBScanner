package com.example.sbscanner.presentation.fragments.dialogs.test

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.sbscanner.databinding.DialogTestBinding
import com.example.sbscanner.presentation.fragments.base.BaseDialogFragment

class TestDialog : BaseDialogFragment<Event, Effect, Command, State>() {

    private lateinit var binding: DialogTestBinding

    override val viewModel: TestViewModel by viewModels { TestViewModel.Factory }

    override val initEvent: Event = Event.Ui.Init

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogTestBinding.inflate(inflater, container, false).apply {
            dialog.cancel.setOnClickListener { dismiss() }
        }
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        isCancelable = false
        return binding.root
    }

    override fun renderState(state: State) = with(binding) {
        dialog.progressInfo.text = "Заполнено - ${state.progress}%"
        dialog.progressBar.progress = state.progress
    }

    override fun handleEffect(effect: Effect) {
        when (effect) {
            is Effect.CloseDialog -> {
                dismiss()
            }
        }
    }

    companion object {
        fun newInstance() = TestDialog()
    }
}
