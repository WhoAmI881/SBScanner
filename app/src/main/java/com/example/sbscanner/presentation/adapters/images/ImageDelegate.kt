package com.example.sbscanner.presentation.adapters.images

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.sbscanner.databinding.ItemImageBinding
import com.example.sbscanner.presentation.adapters.base.AdapterDelegate
import com.example.sbscanner.presentation.adapters.base.DelegateItem

class ImageDelegate : AdapterDelegate {

    override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
        return ViewHolder.create(parent)
    }

    override fun onBindViewHolder(
        holder: RecyclerView.ViewHolder, item: DelegateItem, position: Int
    ) {
        (holder as ViewHolder).bind(item.content() as ImageItem)
    }

    override fun isOfViewType(item: DelegateItem): Boolean {
        return item is ImageDelegateItem
    }

    override fun onPayloadBindViewHolder(
        holder: RecyclerView.ViewHolder,
        item: DelegateItem,
        position: Int
    ) {

    }

    class ViewHolder(private val binding: ItemImageBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(model: ImageItem) = with(binding) {
            Glide.with(root).load(model.file).into(image)
        }

        companion object {
            fun create(parent: ViewGroup): ViewHolder {
                return ViewHolder(
                    ItemImageBinding.inflate(
                        LayoutInflater.from(parent.context), parent, false
                    )
                )
            }
        }
    }
}
