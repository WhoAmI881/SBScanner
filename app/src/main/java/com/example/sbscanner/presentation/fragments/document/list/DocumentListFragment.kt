package com.example.sbscanner.presentation.fragments.document.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sbscanner.App
import com.example.sbscanner.databinding.FragmentDocumentListBinding
import com.example.sbscanner.domain.utils.EMPTY_ID
import com.example.sbscanner.presentation.adapters.documents.DocumentDelegate
import com.example.sbscanner.presentation.adapters.documents.DocumentsAdapter
import com.example.sbscanner.presentation.adapters.documents.DocumentsAdapterListener
import com.example.sbscanner.presentation.adapters.empty.EmptyDelegate
import com.example.sbscanner.presentation.fragments.base.BaseFragment
import com.example.sbscanner.presentation.fragments.base.DialogListener
import com.example.sbscanner.presentation.fragments.dialogs.delete.box.DeleteBoxDialog
import com.example.sbscanner.presentation.fragments.dialogs.delete.document.DeleteDocumentDialog
import com.example.sbscanner.presentation.fragments.dialogs.form.document.FormDocumentDialog
import com.example.sbscanner.presentation.navigation.Presenter

class DocumentListFragment : BaseFragment<Event, Effect, Command, State>() {

    private lateinit var binding: FragmentDocumentListBinding

    override val viewModel: DocumentListViewModel by viewModels { DocumentListViewModel.Factory }

    override lateinit var initEvent: Event

    private lateinit var adapter: DocumentsAdapter

    private val presenter = Presenter(App.INSTANCE.router)

    private val actionListener = object : DocumentsAdapterListener {

        override fun onEditDocument(position: Int) {
            viewModel.commitEvent(Event.Ui.EditDocClick(position))
        }

        override fun onRemoveDocument(position: Int) {
            viewModel.commitEvent(Event.Ui.RemoveDocClick(position))
        }

        override fun onDocumentImagesShow(position: Int) {
            viewModel.commitEvent(Event.Ui.ShowDocumentImagesClick(position))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val boxId = arguments?.getInt(KEY_BOX) ?: EMPTY_ID
        initEvent = Event.Ui.Init(boxId)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        adapter = DocumentsAdapter(actionListener).apply {
            addDelegate(DocumentDelegate())
            addDelegate(EmptyDelegate())
        }
        binding = FragmentDocumentListBinding.inflate(inflater, container, false).apply {
            recycle.layoutManager = LinearLayoutManager(requireContext())
            recycle.adapter = adapter

            addDocument.setOnClickListener {
                viewModel.commitEvent(Event.Ui.AddDocClick)
            }
            addImage.setOnClickListener {
                viewModel.commitEvent(Event.Ui.AddImageClick)
            }
            back.setOnClickListener {
                viewModel.commitEvent(Event.Ui.ReturnBack)
            }
        }
        return binding.root
    }

    override fun renderState(state: State) = with(binding) {
        if (state.imgEnable) {
            addImage.alpha = 1f
        } else {
            addImage.alpha = 0.5f
        }
        addImage.isClickable = state.imgEnable
        adapter.submitList(state.delegates)
    }

    override fun handleEffect(effect: Effect) {
        when (effect) {
            is Effect.OpenDeleteDocDialog -> {
                val dialog = DeleteDocumentDialog.newInstance(effect.docId)
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
            is Effect.OpenDocumentScanner -> {
                presenter.onAddDocumentsOpen(effect.boxId)
            }
            is Effect.OpenEditDocument -> {
                val dialog = FormDocumentDialog.newInstance(effect.boxId, effect.docId)
                dialog.show(childFragmentManager, FormDocumentDialog::class.simpleName)
            }
            is Effect.OpenImageList -> {
                presenter.onImageListOpen(effect.docId)
            }
            is Effect.OpenImageScanner -> {
                presenter.onAddImagesOpen(effect.boxId)
            }
            is Effect.ReturnBack -> {
                presenter.back()
            }
        }
    }

    companion object {

        private const val KEY_BOX = "KEY_BOX"

        fun newInstance(boxId: Int) = DocumentListFragment().apply {
            val args = Bundle()
            args.putInt(KEY_BOX, boxId)
            arguments = args
        }
    }
}
