package com.example.sbscanner.presentation.fragments.document.scanner

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.sbscanner.App
import com.example.sbscanner.databinding.FragmentDocumentScannerBinding
import com.example.sbscanner.domain.utils.EMPTY_ID
import com.example.sbscanner.presentation.fragments.base.*
import com.example.sbscanner.presentation.fragments.dialogs.form.document.FormDocListener
import com.example.sbscanner.presentation.fragments.dialogs.form.document.FormDocumentDialog
import com.example.sbscanner.presentation.navigation.Presenter
import com.example.sbscanner.presentation.utils.showDialogMessage

class DocumentScannerFragment : CameraFragment<Event, Effect, Command, State>() {

    private lateinit var binding: FragmentDocumentScannerBinding

    override val viewModel: DocumentScannerViewModel by viewModels {
        DocumentScannerViewModel.Factory
    }

    override fun getSurfaceTexture(): AutoFitSurfaceView {
        return binding.camera.holder
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

    override fun handleCameraEvent(event: CameraEvent) {
        when (event) {
            is CameraEvent.StartInit -> {
                viewModel.commitEvent(Event.Ui.ChangeCameraState(CameraState.INIT))
            }
            is CameraEvent.FailedInit -> {
                viewModel.commitEvent(Event.Ui.ChangeCameraState(CameraState.FAILED))
            }
            is CameraEvent.SuccessInit -> {
                viewModel.commitEvent(Event.Ui.CameraInit(event.cameraScanner))
            }
        }
    }

    override fun renderState(state: State) = with(binding) {
        when (state.cameraState) {
            CameraState.INIT -> camera.initState()
            CameraState.FAILED -> camera.failedState()
            CameraState.SUCCESS -> when (state.formState) {
                FormState.SCANNING -> {
                    camera.scanningState()
                }
                FormState.BARCODE_FOUND -> {
                    camera.detectedBarcodeState()
                }
                else -> {}
            }
        }
    }

    override fun handleEffect(effect: Effect) {
        when (effect) {
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

        fun newInstance(boxId: Int): DocumentScannerFragment {
            return DocumentScannerFragment().apply {
                val args = Bundle()
                args.putInt(KEY_BOX, boxId)
                arguments = args
            }
        }
    }
}
