package com.example.sbscanner.presentation.fragments.start

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.asFlow
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo
import androidx.work.WorkManager
import androidx.work.await
import com.example.sbscanner.App
import com.example.sbscanner.data.remote.service.UploadService
import com.example.sbscanner.data.remote.service.UploadTaskManager
import com.example.sbscanner.databinding.FragmentStartBinding
import com.example.sbscanner.presentation.fragments.base.BaseFragment
import com.example.sbscanner.presentation.navigation.Presenter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.onEach

class StartFragment : BaseFragment<Event, Effect, Command, State>() {

    private lateinit var binding: FragmentStartBinding

    override val viewModel: StartViewModel by viewModels { StartViewModel.Factory }

    override lateinit var initEvent: Event

    private val presenter = Presenter(App.INSTANCE.router)

    private val activityResultLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val permissionGranted = permissions.entries.all {
                it.key in REQUIRED_PERMISSIONS && it.value
            }
            viewModel.commitEvent(Event.Ui.OnPermissionsChange(permissionGranted))
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        /*
        val workManager = WorkManager.getInstance(requireContext())
        val request = OneTimeWorkRequestBuilder<UploadTaskManager>().build()
        workManager.enqueueUniqueWork(
            UploadTaskManager.UPLOAD_TASK_NAME,
            ExistingWorkPolicy.KEEP,
            request
        )
        val state = workManager.getWorkInfoById(request.id).await().state.isFinished
        workManager.getWorkInfoByIdLiveData(request.id).asFlow().filterNotNull().onEach {
        }
         */
        initEvent = Event.Ui.Init(
            allPermissionsGranted = allPermissionsGranted(),
            uploadServiceIsRunning = UploadService.isServiceRunning(requireContext())
        )
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStartBinding.inflate(inflater, container, false).apply {
            start.setOnClickListener { requestPermissions() }
        }
        return binding.root
    }

    override fun renderState(state: State) = with(binding) {
        start.isVisible = state.allPermissionsGranted.not()
    }

    override fun handleEffect(effect: Effect) {
        when (effect) {
            is Effect.RequestPermissions -> requestPermissions()

            is Effect.AddTaskOpen -> presenter.onAddTaskOpen()

            is Effect.BoxListOpen -> presenter.onBoxListOpen(effect.taskId)

            is Effect.ProgressSendingTaskOpen -> presenter.onProgressSendingTaskOpen(effect.taskId)
        }
    }

    private fun requestPermissions() {
        activityResultLauncher.launch(REQUIRED_PERMISSIONS)
    }

    companion object {
        @JvmStatic
        fun newInstance() = StartFragment()

        private val REQUIRED_PERMISSIONS =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                mutableListOf(
                    Manifest.permission.POST_NOTIFICATIONS,
                    Manifest.permission.CAMERA
                ).toTypedArray()
            } else {
                mutableListOf(
                    Manifest.permission.CAMERA,
                ).toTypedArray()
            }
    }
}
