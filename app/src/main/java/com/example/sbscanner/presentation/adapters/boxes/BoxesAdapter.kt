package com.example.sbscanner.presentation.adapters.boxes

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.sbscanner.presentation.adapters.base.AdapterDelegate
import com.example.sbscanner.presentation.adapters.base.DelegateAdapterItemDiffCallback
import com.example.sbscanner.presentation.adapters.base.DelegateItem

interface BoxesAdapterListener {
    fun onBoxClick(position: Int)
    fun onDeleteBoxClick(position: Int)
}

class BoxesAdapter(private val actionListener: BoxesAdapterListener) :
    ListAdapter<DelegateItem, RecyclerView.ViewHolder>(
        DelegateAdapterItemDiffCallback()
    ) {

    private val delegates: MutableList<AdapterDelegate> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val holder = delegates[viewType].onCreateViewHolder(parent)
        if (holder is BoxDelegate.ViewHolder) {
            holder.setOnClickDeleteListener {
                if (holder.adapterPosition != RecyclerView.NO_POSITION) {
                    actionListener.onDeleteBoxClick(holder.adapterPosition)
                }
            }
            holder.setOnClickItemListener {
                if (holder.adapterPosition != RecyclerView.NO_POSITION) {
                    actionListener.onBoxClick(holder.adapterPosition)
                }
            }
        }
        return holder
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        delegates[getItemViewType(position)].onBindViewHolder(holder, getItem(position), position)
    }

    override fun getItemViewType(position: Int): Int {
        return delegates.indexOfFirst { it.isOfViewType(currentList[position]) }
    }

    fun addDelegate(delegate: AdapterDelegate) {
        delegates.add(delegate)
    }
}
