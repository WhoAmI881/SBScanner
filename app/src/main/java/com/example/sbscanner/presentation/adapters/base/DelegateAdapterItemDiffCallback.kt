package com.example.sbscanner.presentation.adapters.base

import androidx.recyclerview.widget.DiffUtil

class DelegateAdapterItemDiffCallback : DiffUtil.ItemCallback<DelegateItem>() {

    override fun areItemsTheSame(oldItem: DelegateItem, newItem: DelegateItem): Boolean {
        return oldItem::class == newItem::class && oldItem.id() == newItem.id()
    }

    override fun areContentsTheSame(oldItem: DelegateItem, newItem: DelegateItem): Boolean {
        return oldItem.compareToOther(newItem)
    }

    override fun getChangePayload(oldItem: DelegateItem, newItem: DelegateItem): Any? {
        return if(oldItem.changePayloads(newItem)) true else null
    }
}
