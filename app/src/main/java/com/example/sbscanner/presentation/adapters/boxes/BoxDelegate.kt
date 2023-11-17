package com.example.sbscanner.presentation.adapters.boxes

import android.view.LayoutInflater
import android.view.View.OnClickListener
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.sbscanner.R
import com.example.sbscanner.databinding.ItemBoxBinding
import com.example.sbscanner.presentation.adapters.base.AdapterDelegate
import com.example.sbscanner.presentation.adapters.base.DelegateItem

class BoxDelegate : AdapterDelegate {

  override fun onCreateViewHolder(parent: ViewGroup): RecyclerView.ViewHolder {
    return ViewHolder.create(parent)
  }

  override fun onBindViewHolder(
    holder: RecyclerView.ViewHolder, item: DelegateItem, position: Int
  ) {
    (holder as ViewHolder).bind(item.content() as BoxItem)
  }

  override fun isOfViewType(item: DelegateItem): Boolean {
    return item is BoxDelegateItem
  }

  override fun onPayloadBindViewHolder(
    holder: RecyclerView.ViewHolder,
    item: DelegateItem,
    position: Int
  ) {
  }

  class ViewHolder(private val binding: ItemBoxBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(model: BoxItem) = with(binding) {
      when (model.state) {
        BoxState.FULL     -> {
          itemTitle.setBackgroundResource(R.drawable.bg_item_title_full)
        }

        BoxState.NOT_FULL -> {
          itemTitle.setBackgroundResource(R.drawable.bg_item_title_not_full)
        }

        BoxState.SENT     -> {
          itemTitle.setBackgroundResource(R.drawable.bg_item_title_sent)
        }
      }
      boxBarcode.text = "${model.barcode}"
      dataCount.text = "Кол-во дел/фото: ${model.docCount}/${model.imgCount}"
      if (model.imgCount == 0) {
        imgSent.isVisible = false
      } else {
        imgSent.text = "Отправлено: ${model.imgSent * 100 / model.imgCount}%"
        imgSent.isVisible = true
      }
    }

    fun setOnClickDeleteListener(action: OnClickListener) {
      binding.deleteBox.setOnClickListener(action)
    }

    fun setOnClickItemListener(action: OnClickListener) {
      binding.parent.setOnClickListener(action)
    }

    companion object {
      fun create(parent: ViewGroup): ViewHolder {
        return ViewHolder(
          ItemBoxBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
          )
        )
      }
    }
  }
}
