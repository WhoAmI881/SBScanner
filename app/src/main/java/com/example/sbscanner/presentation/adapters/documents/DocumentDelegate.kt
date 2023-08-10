package com.example.sbscanner.presentation.adapters.documents

import android.view.LayoutInflater
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.sbscanner.R
import com.example.sbscanner.databinding.ItemDocumentBinding
import com.example.sbscanner.presentation.adapters.base.AdapterDelegate
import com.example.sbscanner.presentation.adapters.base.DelegateItem

class DocumentDelegate : AdapterDelegate {

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return ViewHolder.create(parent)
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder, item: DelegateItem, position: Int
    ) {
        (holder as ViewHolder).bind(item.content() as DocumentItem)
    }

    override fun isOfViewType(item: DelegateItem): Boolean {
        return item is DocumentDelegateItem
    }

    override fun onPayloadBindViewHolder(
        holder: RecyclerView.ViewHolder,
        item: DelegateItem,
        position: Int
    ) {

    }

    class ViewHolder(private val binding: ItemDocumentBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(model: DocumentItem) = with(binding) {
            docBarcode.text = model.barcode
            imgCount.text = "Количество фото: ${model.imageCount}"
            when (model.state) {
                DocState.FULL -> {
                    itemTitle.setBackgroundResource(R.drawable.bg_item_title_full)
                }
                DocState.NOT_FULL -> {
                    itemTitle.setBackgroundResource(R.drawable.bg_item_title_not_full)
                }
                DocState.SENT -> {
                    itemTitle.setBackgroundResource(R.drawable.bg_item_title_sent)
                }
            }
        }

        fun setOnClickEditListener(listener: OnClickListener) {
            binding.docEdit.setOnClickListener(listener)
        }

        companion object {
            fun create(parent: ViewGroup): ViewHolder {
                return ViewHolder(
                    ItemDocumentBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    )
                )
            }
        }
    }
}
