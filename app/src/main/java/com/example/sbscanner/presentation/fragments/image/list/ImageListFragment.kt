package com.example.sbscanner.presentation.fragments.image.list

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.sbscanner.App
import com.example.sbscanner.databinding.FragmentImageListBinding
import com.example.sbscanner.domain.utils.EMPTY_ID
import com.example.sbscanner.presentation.adapters.empty.EmptyDelegate
import com.example.sbscanner.presentation.adapters.images.ImageDelegate
import com.example.sbscanner.presentation.adapters.images.ImagesAdapter
import com.example.sbscanner.presentation.adapters.images.ImagesAdapterListener
import com.example.sbscanner.presentation.fragments.base.BaseFragment
import com.example.sbscanner.presentation.navigation.Presenter

class ImageListFragment : BaseFragment<Event, Effect, Command, State>() {

    private lateinit var binding: FragmentImageListBinding

    private lateinit var adapter: ImagesAdapter

    override val viewModel: ImageListViewModel by viewModels { ImageListViewModel.Factory }

    override lateinit var initEvent: Event

    private val presenter = Presenter(App.INSTANCE.router)

    private val actionListener = object : ImagesAdapterListener {

        override fun onImageClick(position: Int) {
            viewModel.commitEvent(Event.Ui.ImageItemClick(position))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val docId = arguments?.getInt(KEY_DOC) ?: EMPTY_ID
        initEvent = Event.Ui.Init(docId)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        adapter = ImagesAdapter(actionListener).apply {
            addDelegate(ImageDelegate())
            addDelegate(EmptyDelegate())
        }

        binding = FragmentImageListBinding.inflate(inflater, container, false).apply {

            back.setOnClickListener {
                viewModel.commitEvent(Event.Ui.BackClick)
            }

            recycle.layoutManager = GridLayoutManager(
                requireContext(),
                3,
                LinearLayoutManager.VERTICAL,
                false
            )
            recycle.adapter = adapter
        }
        return binding.root
    }

    override fun renderState(state: State) = with(binding) {
        emptyItem.root.isVisible = state.isEmpty
        recycle.isVisible = state.isEmpty.not()
        adapter.submitList(state.delegates)
    }

    override fun handleEffect(effect: Effect) {
        when (effect) {
            is Effect.OpenImage -> {
                presenter.onImageInfoOpen(effect.imgId)
            }
            is Effect.ReturnBack -> {
                presenter.back()
            }
        }
    }

    companion object {

        private const val KEY_DOC = "KEY_DOC"

        fun newInstance(docId: Int) = ImageListFragment().apply {
            val args = Bundle()
            args.putInt(KEY_DOC, docId)
            arguments = args
        }
    }
}
