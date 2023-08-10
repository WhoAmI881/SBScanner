package com.example.sbscanner.presentation.fragments.document.info

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.example.sbscanner.App
import com.example.sbscanner.databinding.FragmentDocumentInfoBinding
import com.example.sbscanner.domain.utils.EMPTY_ID
import com.example.sbscanner.presentation.fragments.base.BaseFragment
import com.example.sbscanner.presentation.navigation.Presenter

class DocumentInfoFragment : BaseFragment<Event, Effect, Command, State>() {

    private lateinit var binding: FragmentDocumentInfoBinding

    private val presenter = Presenter(App.INSTANCE.router)

    override val viewModel: DocumentInfoViewModel by viewModels { DocumentInfoViewModel.Factory }

    override lateinit var initEvent: Event

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            val boxId = it.getInt(KEY_BOX, EMPTY_ID)
            val docId = it.getInt(KEY_DOC, EMPTY_ID)
            val docBarcode = it.getString(KEY_DOC_BARCODE, "")
            initEvent = if(docBarcode.isBlank()){
                Event.Ui.InitEdit(boxId, docId)
            }else{
                Event.Ui.InitAdd(boxId, docBarcode)
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDocumentInfoBinding.inflate(inflater, container, false).apply {
            form.docDate.setOnClickListener {
                form.openDatePicker(requireContext())
            }
            form.save.setOnClickListener {
                viewModel.commitEvent(Event.Ui.SaveDocClick(form.getFormData()))
            }
            form.cancel.setOnClickListener {
                viewModel.commitEvent(Event.Ui.CancelClick)
            }
        }
        return binding.root
    }

    override fun renderState(state: State) = with(binding.form) {
        setFormData(state.formData)
    }

    override fun handleEffect(effect: Effect) {
        when (effect) {
            is Effect.ReturnBack -> {
                presenter.back()
            }
        }
    }

    companion object {

        private const val KEY_DOC = "KEY_DOC"

        private const val KEY_DOC_BARCODE = "KEY_DOC_BARCODE"

        private const val KEY_BOX = "KEY_BOX"

        fun newInstance(boxId: Int, docId: Int) = DocumentInfoFragment().apply {
            val args = Bundle()
            args.putInt(KEY_BOX, boxId)
            args.putInt(KEY_DOC, docId)
            arguments = args
        }

        fun newInstance(boxId: Int, docBarcode: String) = DocumentInfoFragment().apply {
            val args = Bundle()
            args.putInt(KEY_BOX, boxId)
            args.putString(KEY_DOC_BARCODE, docBarcode)
            arguments = args
        }
    }
}
