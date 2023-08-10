package com.example.sbscanner.presentation.adapters.images

import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.sbscanner.presentation.adapters.base.AdapterDelegate
import com.example.sbscanner.presentation.adapters.base.DelegateAdapterItemDiffCallback
import com.example.sbscanner.presentation.adapters.base.DelegateItem

interface ImagesAdapterListener {
    fun onImageClick(position: Int)
}

class ImagesAdapter(private val actionListener: ImagesAdapterListener) :
    ListAdapter<DelegateItem, RecyclerView.ViewHolder>(
        DelegateAdapterItemDiffCallback()
    ) {

    private val delegates: MutableList<AdapterDelegate> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val holder = delegates[viewType].onCreateViewHolder(parent)
        if(holder is ImageDelegate.ViewHolder){
            holder.itemView.setOnClickListener {
                if (holder.adapterPosition != RecyclerView.NO_POSITION) {
                    actionListener.onImageClick(holder.adapterPosition)
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
