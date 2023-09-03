package com.example.sbscanner.presentation.fragments.task.upload

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.example.sbscanner.App
import com.example.sbscanner.data.remote.service.ServiceActions
import com.example.sbscanner.data.remote.service.UploadService
import com.example.sbscanner.databinding.FragmentTaskUploadBinding
import com.example.sbscanner.domain.utils.EMPTY_ID
import com.example.sbscanner.presentation.fragments.base.BaseFragment
import com.example.sbscanner.presentation.navigation.Presenter
import com.example.sbscanner.presentation.utils.showDialogConfirm
import com.example.sbscanner.presentation.utils.showDialogMessage

class TaskUploadFragment : BaseFragment<Event, Effect, Command, State>() {

    private lateinit var binding: FragmentTaskUploadBinding

    private val presenter = Presenter(App.INSTANCE.router)

    override val viewModel: TaskUploadViewModel by viewModels { TaskUploadViewModel.Factory }

    override lateinit var initEvent: Event

    private val receiver = object : BroadcastReceiver() {

        override fun onReceive(context: Context?, intent: Intent?) {
            when (intent?.action) {
                ServiceActions.STOP_SEND.action -> {
                    presenter.back()
                }
                ServiceActions.START_SEND.action -> {
                    val imgCount = intent.getIntExtra(intent.action, 0)
                    viewModel.commitEvent(Event.Ui.StartSend(imgCount))
                }
                ServiceActions.SENT_IMAGE.action -> {
                    val progress = intent.getIntExtra(intent.action, 0)
                    viewModel.commitEvent(Event.Ui.ImageSent(progress))
                }
                ServiceActions.SUCCESS_SEND.action -> {
                    viewModel.commitEvent(Event.Ui.SuccessSendTask)
                }
                ServiceActions.SERVER_ERROR.action -> {
                    viewModel.commitEvent(Event.Ui.ErrorSendTask)
                }
                ServiceActions.LOSE_CONNECTION.action -> {
                    viewModel.commitEvent(Event.Ui.LoseConnection)
                }
                else -> {}
            }
        }
    }

    private fun stopUploadTask() {
        requireContext().showDialogConfirm(
            "Отмена отправки задания",
            "Вы действительно хотите отменить отправку?",
            {
                UploadService.stopService(requireContext())
                presenter.back()
            },
            {}
        )
    }

    private val backPressedCallback = object : OnBackPressedCallback(true) {
        override fun handleOnBackPressed() {
            stopUploadTask()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val taskId = arguments?.getInt(KEY_TASK) ?: EMPTY_ID
        val serviceIsRunning = arguments?.getBoolean(KEY_SERVICE, false) ?: false

        initEvent = Event.Ui.Init(taskId, serviceIsRunning)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTaskUploadBinding.inflate(inflater, container, false).apply {
            stopUpload.setOnClickListener {
                stopUploadTask()
            }
            reload.setOnClickListener {
                viewModel.commitEvent(Event.Ui.ReloadClick)
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, backPressedCallback)
        return binding.root
    }

    override fun onResume() {
        super.onResume()
        if (UploadService.isServiceRunning(requireContext())
                .not() && viewModel.currentState.serviceIsRunning
        ) {
            presenter.back()
        }
        val filter = IntentFilter().apply {
            ServiceActions.values().forEach {
                addAction(it.action)
            }
        }
        LocalBroadcastManager.getInstance(requireContext())
            .registerReceiver(receiver, filter)
    }

    override fun onPause() {
        super.onPause()
        LocalBroadcastManager.getInstance(requireContext()).unregisterReceiver(receiver)
    }

    override fun renderState(state: State) = with(binding) {
        when (state.fragmentState) {
            FragmentState.INIT_UPLOAD -> {
                progressConnect.isVisible = true
                progressBarUpload.isVisible = false
                reload.isVisible = false
                messageText.text = "Авторизация задания..."
            }
            FragmentState.PROGRESS_SENDING -> {
                progressConnect.isVisible = false
                progressBarUpload.isVisible = true
                reload.isVisible = false
                messageText.text = "Передача коробов"
                progressBarUpload.progress = state.progress
                progressCount.text = "${state.progress}%"
            }
            FragmentState.SUCCESS_SEND -> {
                progressConnect.isVisible = false
                progressBarUpload.isVisible = true
                reload.isVisible = false
                progressBarUpload.progress = state.progress
                progressCount.text = "Задание отправлено - ${state.progress}%"
                stopUpload.setOnClickListener { presenter.back() }
                backPressedCallback.remove()
            }
            FragmentState.LOSE_CONNECTION -> {
                progressConnect.isVisible = true
                progressBarUpload.isVisible = false
                reload.isVisible = false
                messageText.text = state.errorType.message
            }
            FragmentState.ERROR -> {
                progressConnect.isVisible = false
                progressBarUpload.isVisible = false
                reload.isVisible = true
                messageText.text = state.errorType.message
            }
        }
    }

    override fun handleEffect(effect: Effect) {
        when (effect) {
            is Effect.StartUploadService -> {
                UploadService.startService(effect.sessionId, effect.taskId, requireContext())
            }
            is Effect.ShowInitTaskError -> {
                requireContext().showDialogMessage(
                    "Ошибка авторизации",
                    effect.msg
                ) {
                    presenter.onEditTaskOpen(effect.taskId)
                }
            }
            is Effect.ShowErrorCode -> {
                requireContext().showDialogMessage(
                    "Server error",
                    "Error code: ${effect.code}"
                ) {}
            }
        }
    }

    companion object {

        private const val KEY_TASK = "KEY_TASK"

        private const val KEY_SERVICE = "KEY_SERVICE"

        fun newInstance(taskId: Int, serviceIsRunning: Boolean) = TaskUploadFragment().apply {
            val args = Bundle()
            args.putInt(KEY_TASK, taskId)
            args.putBoolean(KEY_SERVICE, serviceIsRunning)
            arguments = args
        }
    }
}
