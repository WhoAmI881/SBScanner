package com.example.sbscanner.presentation.fragments.option

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.sbscanner.App
import com.example.sbscanner.R
import com.example.sbscanner.data.local.files.UrlOption
import com.example.sbscanner.databinding.FragmentOptionBinding
import com.example.sbscanner.presentation.navigation.Presenter
import com.example.sbscanner.presentation.utils.showDialogConfirm
import com.example.sbscanner.presentation.utils.showSnackbar
import com.jakewharton.processphoenix.ProcessPhoenix

class OptionFragment : Fragment() {

    private val fileManager = App.INSTANCE.fileManager
    private val presenter = Presenter(App.INSTANCE.router)
    private lateinit var binding: FragmentOptionBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val option = fileManager.getUrlOption()
        binding = FragmentOptionBinding.inflate(inflater, container, false).apply {
            baseUrl.setText(option.baseUrl)
            port.setText(option.port.toString())
            confirm.setOnClickListener {
                val baseUrl = baseUrl.text.toString().trim()
                val port = port.text.toString().trim()
                if(baseUrl.isEmpty() || port.isEmpty()){
                    requireView().showSnackbar(resources.getString(R.string.empty_task))
                }else{
                    requireContext().showDialogConfirm(
                        "Обновление конфигурации",
                        "После обновления конфигурации приложение перезапустится",
                        {
                            fileManager.updateUrlOption(UrlOption(baseUrl, port.toInt()))
                            ProcessPhoenix.triggerRebirth(requireContext())
                        },
                        {}
                    )
                }
            }
            cancel.setOnClickListener {
                presenter.back()
            }
        }
        return binding.root
    }

    companion object {

        @JvmStatic
        fun newInstance() = OptionFragment()
    }
}