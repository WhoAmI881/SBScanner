package com.example.sbscanner.presentation.fragments.image.scanner

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.sbscanner.App
import com.example.sbscanner.databinding.FragmentImageScannerBinding
import com.example.sbscanner.domain.utils.EMPTY_ID
import com.example.sbscanner.presentation.fragments.base.*
import com.example.sbscanner.presentation.fragments.dialogs.form.document.FormDocListener
import com.example.sbscanner.presentation.fragments.dialogs.form.document.FormDocumentDialog
import com.example.sbscanner.presentation.fragments.dialogs.form.image.FormImageDialog
import com.example.sbscanner.presentation.fragments.dialogs.form.image.FormImageListener
import com.example.sbscanner.presentation.navigation.Presenter
import com.example.sbscanner.presentation.utils.showDialogMessage

class ImageScannerFragment : CameraFragment<Event, Effect, Command, State>() {

    private lateinit var binding: FragmentImageScannerBinding

    override val viewModel: ImageScannerViewModel by viewModels { ImageScannerViewModel.Factory }

    override lateinit var initEvent: Event

    override fun getSurfaceTexture(): AutoFitSurfaceView {
        return binding.camera.holder
    }

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

    override fun renderState(state: State): Unit = with(binding) {
        when (state.cameraState) {
            CameraState.INIT -> {
                camera.setBackAction { presenter.back() }
                camera.initState()
            }
            CameraState.FAILED -> {
                camera.setBackAction { presenter.back() }
                camera.failedState()
            }
            CameraState.SUCCESS -> when (state.formState) {
                FormState.SCANNING -> {
                    camera.scanningState()
                }
                FormState.BARCODE_FOUND -> {
                    camera.detectedBarcodeState()
                }
                FormState.TAKE_PHOTO -> {
                    camera.takePhotoState {
                        viewModel.commitEvent(Event.Ui.TakePhotoClick)
                    }
                }
            }
        }
    }

    override fun handleEffect(effect: Effect) {
        when (effect) {
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

        fun newInstance(boxId: Int) = ImageScannerFragment().apply {
            val args = Bundle()
            args.putInt(KEY_BOX, boxId)
            arguments = args
        }
    }
}
