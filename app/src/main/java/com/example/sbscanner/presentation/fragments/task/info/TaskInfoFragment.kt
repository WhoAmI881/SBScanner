package com.example.sbscanner.presentation.fragments.task.info

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.PopupMenu
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResultListener
import androidx.fragment.app.viewModels
import com.example.sbscanner.App
import com.example.sbscanner.R
import com.example.sbscanner.databinding.FragmentTaskInfoBinding
import com.example.sbscanner.domain.utils.EMPTY_ID
import com.example.sbscanner.domain.utils.isNotEmptyId
import com.example.sbscanner.presentation.fragments.base.BaseFragment
import com.example.sbscanner.presentation.fragments.dialogs.delete.task.DeleteTaskDialog
import com.example.sbscanner.presentation.fragments.dialogs.test.TestDialog
import com.example.sbscanner.presentation.navigation.Presenter
import com.example.sbscanner.presentation.utils.setIfNotEqual
import com.example.sbscanner.presentation.utils.showDialogConfirm
import com.example.sbscanner.presentation.utils.showSnackbar

class TaskInfoFragment : BaseFragment<Event, Effect, Command, State>() {

    private lateinit var binding: FragmentTaskInfoBinding

    override val viewModel: TaskInfoViewModel by viewModels { TaskInfoViewModel.Factory }

    private val presenter = Presenter(App.INSTANCE.router)

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
        binding = FragmentTaskInfoBinding.inflate(inflater, container, false).apply {
            deleteTask.setOnClickListener {
                showPopupMenu(it)
            }
            confirm.setOnClickListener {
                hideKeyboard()
                viewModel.commitEvent(
                    Event.Ui.ConfirmClick(
                        taskBarcode = valueBarcode.text.toString().trim(),
                        userId = userId.text.toString().trim()
                    )
                )
            }
            cancel.setOnClickListener { presenter.back() }
            taskBarcode.setOnClickListener {
                setScannerResultListener(KEY_REQUEST, KEY_BUNDLE) {
                    viewModel.commitEvent(Event.Ui.BarcodeTaskReceived(it))
                }
                presenter.onTaskScannerOpen()
            }
            userIdIcon.setOnClickListener {
                setScannerResultListener(KEY_REQUEST, KEY_BUNDLE) {
                    viewModel.commitEvent(Event.Ui.BarcodeUserIdReceived(it))
                }
                presenter.onTaskScannerOpen()
            }
            taskIcon.setOnClickListener {
                val dialog = TestDialog.newInstance()
                dialog.show(childFragmentManager, TestDialog::class.simpleName)
            }
            userId.doOnTextChanged { text, _, _, _ ->
                text?.let { viewModel.commitEvent(Event.Ui.InputUserId(it.toString())) }
            }
        }
        return binding.root
    }

    override fun renderState(state: State) = with(binding) {
        userId.setIfNotEqual(state.userId)
        valueBarcode.text = state.taskBarcode
    }

    override fun handleEffect(effect: Effect) = when (effect) {
        is Effect.OpenBoxList -> {
            presenter.onBoxListOpen(effect.taskId)
        }
        is Effect.ReturnBack -> {
            presenter.back()
        }
        is Effect.ErrorUpdate -> {
            requireView().showSnackbar(resources.getString(R.string.error_update_task))
        }
        is Effect.EmptyData -> {
            requireView().showSnackbar(resources.getString(R.string.empty_task))
        }
        is Effect.OpenTaskDeleteDialog -> {
            val dialog = DeleteTaskDialog.newInstance(effect.taskId)
            dialog.show(childFragmentManager, DeleteTaskDialog::class.simpleName)
        }
    }

    private fun hideKeyboard() {
        val imm = activity?.getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        var view = activity?.currentFocus
        if (view == null) {
            view = View(activity)
        }
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    private fun Fragment.setScannerResultListener(
        requestKey: String,
        bundleKey: String,
        receivedListener: (String) -> Unit
    ) {
        this.setFragmentResultListener(requestKey) { _, bundle ->
            bundle.getString(bundleKey)?.let {
                receivedListener(it)
            }
        }
    }

    private fun showPopupMenu(view: View) {
        val popupMenu = PopupMenu(view.context, view)

        popupMenu.menu.add(0, ID_EDIT, Menu.NONE, "Конфигурация")
        popupMenu.menu.add(0, ID_DELETE, Menu.NONE, "Удалить задание")
        popupMenu.menu.findItem(ID_DELETE).isEnabled = viewModel.currentState.taskId.isNotEmptyId()

        popupMenu.setOnMenuItemClickListener {
            when (it.itemId) {
                ID_EDIT -> {
                    presenter.onOptionOpen()
                }
                ID_DELETE -> {
                    requireContext().showDialogConfirm(
                        "Удаление задания",
                        "Вы действительно хотите удалить задание?",
                        { viewModel.commitEvent(Event.Ui.DeleteTaskClick) },
                        {}
                    )
                }
            }
            return@setOnMenuItemClickListener true
        }
        popupMenu.show()
    }

    companion object {

        private const val ID_DELETE = 1

        private const val ID_EDIT = 2

        private const val KEY_TASK = "KEY_TASK"

        const val KEY_REQUEST = "KEY_GET_BARCODE"

        const val KEY_BUNDLE = "KEY_BARCODE"

        fun newInstance(taskId: Int): TaskInfoFragment {
            return TaskInfoFragment().apply {
                val args = Bundle()
                args.putInt(KEY_TASK, taskId)
                arguments = args
            }
        }
    }
}
