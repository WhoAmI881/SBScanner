package com.example.sbscanner.presentation.fragments.base

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.launch

interface DialogListener{
    fun onShowListener()
    fun onDismissListener()
}

abstract class BaseDialogFragment<Event : Any, Effect : Any, Command : Any, State : Any> :
    DialogFragment() {

    abstract val viewModel: BaseViewModel<Event, Effect, Command, State>

    abstract val initEvent: Event

    abstract fun renderState(state: State)

    abstract fun handleEffect(effect: Effect)

    var dialogListener: DialogListener? = null

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
        isCancelable = false
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    }

    override fun show(manager: FragmentManager, tag: String?) {
        dialogListener?.onShowListener()
        super.show(manager, tag)
    }

    override fun dismiss() {
        dialogListener?.onDismissListener()
        super.dismiss()
    }

    fun dismissWithAction(action: () -> Unit){
        action()
        dismiss()
    }
}
