package com.example.sbscanner.presentation.adapters.empty

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.sbscanner.databinding.ItemEmptyBinding
import com.example.sbscanner.presentation.adapters.base.AdapterDelegate
import com.example.sbscanner.presentation.adapters.base.DelegateItem

class EmptyDelegate : AdapterDelegate {

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return ViewHolder.create(parent)
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder, item: DelegateItem, position: Int
    ) {
        (holder as ViewHolder).bind(item.content() as EmptyItem)
    }

    override fun isOfViewType(item: DelegateItem): Boolean {
        return item is EmptyDelegateItem
    }

    override fun onPayloadBindViewHolder(
        holder: RecyclerView.ViewHolder,
        item: DelegateItem,
        position: Int
    ) {

    }

    class ViewHolder(private val binding: ItemEmptyBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(model: EmptyItem) = with(binding) {

        }

        companion object {
            fun create(parent: ViewGroup): ViewHolder {
                return ViewHolder(
                    ItemEmptyBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    )
                )
            }
        }
    }
}
