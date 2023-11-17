package com.example.sbscanner.presentation.fragments.image.scanner

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.camera.view.PreviewView
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import com.example.sbscanner.App
import com.example.sbscanner.R
import com.example.sbscanner.databinding.FragmentImageScannerBinding
import com.example.sbscanner.domain.utils.EMPTY_ID
import com.example.sbscanner.presentation.fragments.base.*
import com.example.sbscanner.presentation.fragments.dialogs.form.document.FormDocListener
import com.example.sbscanner.presentation.fragments.dialogs.form.document.FormDocumentDialog
import com.example.sbscanner.presentation.fragments.dialogs.form.image.FormImageDialog
import com.example.sbscanner.presentation.fragments.dialogs.form.image.FormImageListener
import com.example.sbscanner.presentation.fragments.test.detectedBarcodeState
import com.example.sbscanner.presentation.fragments.test.failedState
import com.example.sbscanner.presentation.fragments.test.initState
import com.example.sbscanner.presentation.fragments.test.scanningState
import com.example.sbscanner.presentation.fragments.test.setBackAction
import com.example.sbscanner.presentation.fragments.test.takePhotoState
import com.example.sbscanner.presentation.navigation.Presenter
import com.example.sbscanner.presentation.utils.onBackPressed
import com.example.sbscanner.presentation.utils.showDialogConfirm
import com.example.sbscanner.presentation.utils.showDialogMessage
import com.google.mlkit.vision.barcode.common.Barcode

class ImageScannerFragment : CameraXFragment<Event, Effect, Command, State>() {

    private lateinit var binding: FragmentImageScannerBinding

    override val previewView: PreviewView
        get() = binding.camera.preview

    override val scanningRegex = REGEX.toRegex()

    override val scanningFormats = intArrayOf(
        Barcode.FORMAT_EAN_13
    )

    override val viewModel: ImageScannerViewModel by viewModels { ImageScannerViewModel.Factory }

    override lateinit var initEvent: Event

    private val presenter = Presenter(App.INSTANCE.router)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val boxId = arguments?.getInt(KEY_BOX) ?: EMPTY_ID
        initEvent = Event.Ui.Init(boxId)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentImageScannerBinding.inflate(inflater, container, false).apply {
            camera.flashButton.isVisible = true
            camera.flashButton.setOnClickListener {
                if(super.flashIsOn) {
                    super.flashOff()
                    camera.flashButton.setImageResource(R.drawable.ic_flash_off)
                } else {
                    super.flashOn()
                    camera.flashButton.setImageResource(R.drawable.ic_flash_on)
                }
            }
        }
        return binding.root
    }

    override fun handleCameraEvent(event: CameraXEvents) {
        when (event) {
            is CameraXEvents.CameraOpening -> {
                viewModel.commitEvent(Event.Ui.CameraStateChange(CameraStateType.INIT))
                binding.camera.setBackAction { presenter.back() }
                onBackPressed { presenter.back() }
            }

            is CameraXEvents.CameraOpen -> {
                viewModel.commitEvent(Event.Ui.CameraStateChange(CameraStateType.OPEN))
                binding.camera.setBackAction { viewModel.commitEvent(Event.Ui.ReturnBack) }
                onBackPressed { viewModel.commitEvent(Event.Ui.ReturnBack) }
            }

            is CameraXEvents.CameraFailed -> {
                viewModel.commitEvent(Event.Ui.CameraStateChange(CameraStateType.FAILED))
            }

            is CameraXEvents.BarcodeFound -> {
                viewModel.commitEvent(Event.Ui.BarcodeFound(event.barcode))
            }

            is CameraXEvents.TakePhoto -> {
                binding.camera.photoButton.isEnabled = true
                viewModel.commitEvent(Event.Ui.PhotoCreated(event.uri))
            }
        }
    }

    override fun renderState(state: State): Unit = with(binding) {
        when (state.cameraStateType) {
            CameraStateType.INIT -> {
                camera.initState()
            }

            CameraStateType.FAILED -> {
                camera.setBackAction { presenter.back() }
                camera.failedState()
            }

            CameraStateType.OPEN -> when (state.formStateType) {
                FormStateType.SCANNING -> {
                    camera.scanningState()
                }

                FormStateType.BARCODE_FOUND -> {
                    camera.detectedBarcodeState()
                }

                FormStateType.TAKE_PHOTO -> {
                    camera.takePhotoState {
                        camera.photoButton.isEnabled = false
                        super.takePhoto()
                    }
                }
            }
        }
    }

    override fun handleEffect(effect: Effect) {
        when (effect) {
            is Effect.StartScanning -> {
                super.startScanning()
            }

            is Effect.StopScanning -> {
                super.stopScanning()
            }

            is Effect.CloseScanning -> {
                presenter.back()
            }

            is Effect.ShowWarningMessage -> {
                requireContext().showDialogConfirm(
                    "Недоделанный короб",
                    "Отсутствует фото у ${effect.emptyDocCount} дел! Завершить добавление фото?",
                    { presenter.back() },
                    { viewModel.commitEvent(Event.Ui.CloseModal) }
                )
            }

            is Effect.ShowErrorFoundMessage -> {
                requireContext().showDialogMessage(
                    "Ошибка добавления фото",
                    "Дело принадлежит другому коробу - ${effect.barcode}",
                ) {
                    viewModel.commitEvent(Event.Ui.CloseModal)
                }
            }

            is Effect.ShowImageDialog -> {
                val dialog = FormImageDialog.newInstance(effect.docId, effect.imgPath)
                dialog.setOnCloseListener(object : FormImageListener {
                    override fun onCancel() {
                        viewModel.commitEvent(Event.Ui.CloseImageForm())
                    }

                    override fun onSave(imgId: Int) {
                        viewModel.commitEvent(Event.Ui.CloseImageForm(imgId))
                    }

                    override fun onDelete() = Unit
                })
                dialog.show(childFragmentManager, FormImageDialog::class.simpleName)
            }

            is Effect.ShowDocDialog -> {
                val dialog = FormDocumentDialog.newInstance(effect.boxId, effect.docBarcode)
                dialog.setOnCloseListener(object : FormDocListener {
                    override fun onCancel() {
                        viewModel.commitEvent(Event.Ui.CloseDocForm())
                    }

                    override fun onSave(docId: Int) {
                        viewModel.commitEvent(Event.Ui.CloseDocForm(docId))
                    }
                })
                dialog.show(childFragmentManager, FormDocumentDialog::class.simpleName)
            }
        }
    }

    companion object {

        private const val KEY_BOX = "KEY_BOX"

        private const val REGEX = "^[A-Za-z0-9]{7,}$"

        fun newInstance(boxId: Int) = ImageScannerFragment().apply {
            val args = Bundle()
            args.putInt(KEY_BOX, boxId)
            arguments = args
        }
    }
}
