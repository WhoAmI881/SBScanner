package com.example.sbscanner.presentation.fragments.document.scanner

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.camera.view.PreviewView
import androidx.fragment.app.viewModels
import com.example.sbscanner.App
import com.example.sbscanner.databinding.FragmentDocumentScannerBinding
import com.example.sbscanner.databinding.TemplateCameraXBinding
import com.example.sbscanner.domain.utils.EMPTY_ID
import com.example.sbscanner.presentation.fragments.base.*
import com.example.sbscanner.presentation.fragments.dialogs.form.document.FormDocListener
import com.example.sbscanner.presentation.fragments.dialogs.form.document.FormDocumentDialog
import com.example.sbscanner.presentation.fragments.test.detectedBarcodeState
import com.example.sbscanner.presentation.fragments.test.failedState
import com.example.sbscanner.presentation.fragments.test.initState
import com.example.sbscanner.presentation.fragments.test.scanningState
import com.example.sbscanner.presentation.fragments.test.setBackAction
import com.example.sbscanner.presentation.navigation.Presenter
import com.example.sbscanner.presentation.utils.showDialogMessage
import com.google.mlkit.vision.barcode.common.Barcode

class DocumentScannerFragment : CameraXFragment<Event, Effect, Command, State>() {

    private lateinit var binding: FragmentDocumentScannerBinding

    override val previewView: PreviewView
        get() = binding.camera.preview

    override val scanningRegex = REGEX.toRegex()

    override val scanningFormats = intArrayOf(
        Barcode.FORMAT_EAN_13
    )

    override val viewModel: DocumentScannerViewModel by viewModels {
        DocumentScannerViewModel.Factory
    }

    private val formListener = object : FormDocListener {
        override fun onCancel() {
            viewModel.commitEvent(Event.Ui.CloseDocForm)
        }

        override fun onSave(docId: Int) {
            viewModel.commitEvent(Event.Ui.CloseDocForm)
        }
    }

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
        binding = FragmentDocumentScannerBinding.inflate(inflater, container, false).apply {
            camera.setBackAction { presenter.back() }
        }
        return binding.root
    }

    override fun handleCameraEvent(event: CameraXEvents) {
        when (event) {
            is CameraXEvents.CameraOpening -> {
                viewModel.commitEvent(Event.Ui.ChangeCameraState(CameraStateType.INIT))
            }

            is CameraXEvents.CameraFailed -> {
                viewModel.commitEvent(Event.Ui.ChangeCameraState(CameraStateType.FAILED))
            }

            is CameraXEvents.CameraOpen -> {
                viewModel.commitEvent(Event.Ui.ChangeCameraState(CameraStateType.OPEN))
            }

            is CameraXEvents.BarcodeFound -> {
                viewModel.commitEvent(Event.Ui.BarcodeFound(event.barcode))
            }

            else -> {}
        }
    }

    override fun renderState(state: State) = with(binding) {
        when (state.cameraStateType) {
            CameraStateType.INIT -> camera.initState()
            CameraStateType.FAILED -> camera.failedState()
            CameraStateType.OPEN -> when (state.formStateType) {
                FormStateType.SCANNING -> {
                    camera.scanningState()
                }

                FormStateType.BARCODE_FOUND -> {
                    camera.detectedBarcodeState()
                }

                else -> {}
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

            is Effect.ShowErrorFoundMessage -> {
                requireContext().showDialogMessage(
                    "Ошибка добавления дела",
                    "Дело принадлежит другому коробу - ${effect.barcode}",
                ) {
                    viewModel.commitEvent(Event.Ui.CloseModal)
                }
            }

            is Effect.OpenDocumentAdd -> {
                val dialog = FormDocumentDialog.newInstance(effect.boxId, effect.barcode)
                dialog.show(childFragmentManager, FormDocumentDialog::class.simpleName)
                dialog.setOnCloseListener(formListener)
            }

            is Effect.OpenDocumentEdit -> {
                val dialog = FormDocumentDialog.newInstance(effect.boxId, effect.docId)
                dialog.show(childFragmentManager, FormDocumentDialog::class.simpleName)
                dialog.setOnCloseListener(formListener)
            }
        }
    }

    companion object {

        private const val KEY_BOX = "KEY_BOX"

        private const val REGEX = "^[A-Za-z0-9]{7,}$"

        fun newInstance(boxId: Int): DocumentScannerFragment {
            return DocumentScannerFragment().apply {
                val args = Bundle()
                args.putInt(KEY_BOX, boxId)
                arguments = args
            }
        }
    }
}
