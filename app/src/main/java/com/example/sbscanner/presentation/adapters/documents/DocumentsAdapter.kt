package com.example.sbscanner.presentation.adapters.documents

import android.view.Menu
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.sbscanner.R
import com.example.sbscanner.presentation.adapters.base.AdapterDelegate
import com.example.sbscanner.presentation.adapters.base.DelegateAdapterItemDiffCallback
import com.example.sbscanner.presentation.adapters.base.DelegateItem

interface DocumentsAdapterListener {
    fun onEditDocument(position: Int)
    fun onRemoveDocument(position: Int)
    fun onDocumentImagesShow(position: Int)
}

class DocumentsAdapter(private val actionListener: DocumentsAdapterListener) :
    ListAdapter<DelegateItem, RecyclerView.ViewHolder>(
        DelegateAdapterItemDiffCallback()
    ) {

    private val delegates: MutableList<AdapterDelegate> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val holder = delegates[viewType].onCreateViewHolder(parent)
        if (holder is DocumentDelegate.ViewHolder) {
            holder.setOnClickEditListener {
                if (holder.adapterPosition != RecyclerView.NO_POSITION) {
                    showPopupMenu(it, holder.adapterPosition)
                }
            }
            holder.itemView.setOnClickListener {
                if (holder.adapterPosition != RecyclerView.NO_POSITION) {
                    actionListener.onDocumentImagesShow(holder.adapterPosition)
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

    private fun showPopupMenu(view: View, position: Int) {
        val popupMenu = PopupMenu(view.context, view)
        val context = view.context

        popupMenu.menu.add(0, ID_EDIT, Menu.NONE, context.getString(R.string.edit))
        popupMenu.menu.add(0, ID_REMOVE, Menu.NONE, context.getString(R.string.remove))

        popupMenu.setOnMenuItemClickListener {
            when (it.itemId) {
                ID_EDIT -> {
                    actionListener.onEditDocument(position)
                }
                ID_REMOVE -> {
                    actionListener.onRemoveDocument(position)
                }
            }
            return@setOnMenuItemClickListener true
        }
        popupMenu.show()
    }

    companion object {
        private const val ID_EDIT = 1
        private const val ID_REMOVE = 2
    }
}
