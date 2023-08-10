package com.example.sbscanner.presentation.fragments.base

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch

abstract class BaseFragment<Event : Any, Effect : Any, Command : Any, State : Any> : Fragment() {

    abstract val viewModel: BaseViewModel<Event, Effect, Command, State>

    abstract val initEvent: Event

    abstract fun renderState(state: State)

    abstract fun handleEffect(effect: Effect)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        with(viewLifecycleOwner.lifecycleScope) {
            launch {
                repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.state.collect { renderState(it) }
                }
            }
            launch {
                repeatOnLifecycle(Lifecycle.State.RESUMED) {
                    viewModel.effects.collect { handleEffect(it) }
                }
            }
        }
        viewModel.setInitEvent(initEvent)
    }

    override fun onResume() {
        super.onResume()
        viewModel.subscribeToCommands()
    }

    override fun onPause() {
        super.onPause()
        viewModel.unsubscribeFromCommands()
    }
}
