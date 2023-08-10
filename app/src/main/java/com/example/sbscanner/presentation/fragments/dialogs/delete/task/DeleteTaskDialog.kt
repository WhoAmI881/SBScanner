package com.example.sbscanner.presentation.fragments.dialogs.delete.task

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.sbscanner.App
import com.example.sbscanner.databinding.DialogDeleteTaskBinding
import com.example.sbscanner.domain.utils.EMPTY_ID
import com.example.sbscanner.presentation.fragments.base.BaseDialogFragment
import com.example.sbscanner.presentation.navigation.Presenter

class DeleteTaskDialog : BaseDialogFragment<Event, Effect, Command, State>() {

    private lateinit var binding: DialogDeleteTaskBinding

    private val presenter = Presenter(App.INSTANCE.router)

    override val viewModel: DeleteTaskViewModel by viewModels { DeleteTaskViewModel.Factory }

    override lateinit var initEvent: Event

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val taskId = arguments?.getInt(KEY_TASK) ?: EMPTY_ID
        initEvent = Event.Ui.Init(taskId)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = DialogDeleteTaskBinding.inflate(inflater, container, false).apply {
            dialog.cancel.setOnClickListener { dismiss() }
        }
        return binding.root
    }

    override fun renderState(state: State) = with(binding) {
        dialog.progressBar.progress = state.progress
        dialog.progressInfo.text = "Удалено: ${state.progress}%"
    }

    override fun handleEffect(effect: Effect) {
        when (effect) {
            Effect.CloseDialog -> {
                dismiss()
            }
            Effect.OpenAddTask -> {
                presenter.onAddTaskOpen()
            }
        }
    }

    companion object {

        private const val KEY_TASK = "KEY_TASK"

        fun newInstance(taskId: Int) = DeleteTaskDialog().apply {
            val args = Bundle()
            args.putInt(KEY_TASK, taskId)
            arguments = args
        }
    }
}
