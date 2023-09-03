package com.example.sbscanner.presentation.fragments.box.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sbscanner.App
import com.example.sbscanner.databinding.FragmentBoxListBinding
import com.example.sbscanner.domain.utils.EMPTY_ID
import com.example.sbscanner.presentation.adapters.boxes.BoxDelegate
import com.example.sbscanner.presentation.adapters.boxes.BoxesAdapter
import com.example.sbscanner.presentation.adapters.boxes.BoxesAdapterListener
import com.example.sbscanner.presentation.adapters.empty.EmptyDelegate
import com.example.sbscanner.presentation.fragments.base.BaseFragment
import com.example.sbscanner.presentation.fragments.base.DialogListener
import com.example.sbscanner.presentation.fragments.dialogs.delete.box.DeleteBoxDialog
import com.example.sbscanner.presentation.fragments.dialogs.form.image.FormImageDialog
import com.example.sbscanner.presentation.navigation.Presenter
import com.example.sbscanner.presentation.utils.onBackPressed
import com.example.sbscanner.presentation.utils.showDialogConfirm

class BoxListFragment : BaseFragment<Event, Effect, Command, State>() {

    private lateinit var binding: FragmentBoxListBinding

    private val presenter = Presenter(App.INSTANCE.router)

    private lateinit var adapter: BoxesAdapter

    override val viewModel: BoxListViewModel by viewModels { BoxListViewModel.Factory }

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
        super.onCreateView(inflater, container, savedInstanceState)

        adapter = BoxesAdapter(boxesAdapterListener).apply {
            addDelegate(BoxDelegate())
            addDelegate(EmptyDelegate())
        }

        binding = FragmentBoxListBinding.inflate(inflater, container, false).apply {
            recycle.layoutManager = LinearLayoutManager(requireContext())
            recycle.adapter = adapter

            sendTask.setOnClickListener {
                viewModel.commitEvent(Event.Ui.SendTaskClick)
            }
            addBox.setOnClickListener {
                viewModel.commitEvent(Event.Ui.AddBoxClick)
            }
            taskParams.setOnClickListener {
                viewModel.commitEvent(Event.Ui.EditTaskClick)
            }
        }
        onBackPressed {
            requireContext().showDialogConfirm(
                "Выход из приложения",
                "Вы действительно хотите выйти?",
                { presenter.back() },
                {}
            )
        }
        return binding.root
    }

    private val boxesAdapterListener = object : BoxesAdapterListener {

        override fun onBoxClick(position: Int) {
            viewModel.commitEvent(Event.Ui.BoxItemClick(position))
        }

        override fun onDeleteBoxClick(position: Int) {
            requireContext().showDialogConfirm(
                "Удаление короба",
                "Вы действительно хотите удалить короб?",
                { viewModel.commitEvent(Event.Ui.DeleteBoxClick(position)) },
                {}
            )
        }
    }

    override fun renderState(state: State) = with(binding) {
        sendTask.alpha = if (state.sendEnable) 1f else 0.5f
        sendTask.isClickable = state.sendEnable
        sendTask.isEnabled = state.sendEnable
        if (state.maxImage == 0) {
            imageCount.isVisible = false
        } else {
            imageCount.isVisible = true
            imageCount.text = "Отправлено фото: ${state.sentImage} из ${state.maxImage}"
        }
        adapter.submitList(state.delegates)
    }

    override fun handleEffect(effect: Effect) {
        when (effect) {
            is Effect.OpenEditTask -> {
                presenter.onEditTaskOpen(effect.taskId)
            }
            is Effect.OpenDeleteBoxDialog -> {
                val dialog = DeleteBoxDialog.newInstance(effect.boxId)
                dialog.setOnDialogListener(object : DialogListener {
                    override fun onShowListener() {
                        viewModel.unsubscribeFromCommands()
                    }

                    override fun onDismissListener() {
                        viewModel.subscribeToCommands()
                    }
                })
                dialog.show(childFragmentManager, DeleteBoxDialog::class.simpleName)
            }
            is Effect.OpenDocumentList -> {
                presenter.onEditDocumentListOpen(effect.boxId)
            }
            is Effect.OpenBoxScanner -> {
                presenter.onBoxScannerOpen(effect.taskId)
            }
            is Effect.OpenTaskUpload -> {
                presenter.onSendTaskOpen(effect.taskId)
            }
        }
    }

    companion object {

        private const val KEY_TASK = "KEY_TASK"

        fun newInstance(taskId: Int) = BoxListFragment().apply {
            val args = Bundle()
            args.putInt(KEY_TASK, taskId)
            arguments = args
        }
    }
}
