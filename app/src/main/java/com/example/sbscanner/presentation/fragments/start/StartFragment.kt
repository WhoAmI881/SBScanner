package com.example.sbscanner.presentation.fragments.start

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.example.sbscanner.App
import com.example.sbscanner.data.remote.service.UploadService
import com.example.sbscanner.databinding.FragmentStartBinding
import com.example.sbscanner.presentation.navigation.Presenter
import kotlinx.coroutines.launch

class StartFragment : Fragment() {

    private lateinit var binding: FragmentStartBinding

    private val viewModel: StartViewModel by viewModels()

    private val presenter = Presenter(App.INSTANCE.router)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        if (allPermissionsGranted()) {
            openTask()
        } else {
            requestPermissions()
        }

        binding = FragmentStartBinding.inflate(inflater, container, false).apply {
            start.setOnClickListener {
                requestPermissions()
            }
        }
        return binding.root
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(requireContext(), it) == PackageManager.PERMISSION_GRANTED
    }

    private val activityResultLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions())
        { permissions ->
            var permissionGranted = true
            permissions.entries.forEach {
                if (it.key in REQUIRED_PERMISSIONS && !it.value) {
                    permissionGranted = false
                }
            }
            if (!permissionGranted) {
                binding.start.isVisible = true
            } else {
                openTask()
            }
        }

    private fun openTask() {
        viewLifecycleOwner.lifecycleScope.launch {
            val tasks = App.INSTANCE.getTaskListUseCase()
            when {
                tasks.isEmpty() -> {
                    presenter.onAddTaskOpen()
                }
                UploadService.isServiceRunning(requireContext()) -> {
                    presenter.onProgressSendingTaskOpen(tasks.last().id)
                }
                else -> {
                    presenter.onBoxListOpen(tasks.last().id)
                }
            }
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
            }else{
                mutableListOf(
                    Manifest.permission.CAMERA,
                ).toTypedArray()
            }
    }
}
