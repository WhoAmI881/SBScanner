package com.example.sbscanner.presentation.fragments.image.info

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import com.bumptech.glide.Glide
import com.example.sbscanner.App
import com.example.sbscanner.databinding.FragmentImageInfoBinding
import com.example.sbscanner.domain.utils.EMPTY_ID
import com.example.sbscanner.presentation.fragments.base.BaseFragment
import com.example.sbscanner.presentation.navigation.Presenter
import java.io.File

class ImageInfoFragment : BaseFragment<Event, Effect, Command, State>() {

    private lateinit var binding: FragmentImageInfoBinding

    private val presenter = Presenter(App.INSTANCE.router)

    override val viewModel: ImageInfoViewModel by viewModels { ImageInfoViewModel.Factory }

    override lateinit var initEvent: Event

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val imgId = arguments?.getInt(KEY_IMG) ?: EMPTY_ID
        initEvent = Event.Ui.Init(imgId)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentImageInfoBinding.inflate(inflater, container, false).apply {
            photo.action.text = "Удалить"
            photo.action.setOnClickListener {
                viewModel.commitEvent(Event.Ui.RemoveClick)
            }
            photo.cancel.setOnClickListener {
                viewModel.commitEvent(Event.Ui.CancelClick)
            }
        }
        return binding.root
    }

    override fun renderState(state: State): Unit = with(binding) {
        val file = File(state.image.path)
        Glide.with(root).load(file).into(photo.image)
    }

    override fun handleEffect(effect: Effect) {
        when (effect) {
            is Effect.ReturnBack -> {
                presenter.back()
            }
        }
    }

    companion object {

        private const val KEY_IMG = "KEY_IMG"

        fun newInstance(imgId: Int) = ImageInfoFragment().apply {
            val args = Bundle()
            args.putInt(KEY_IMG, imgId)
            arguments = args
        }
    }
}
