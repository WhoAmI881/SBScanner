package com.example.sbscanner.presentation.adapters.images

import com.example.sbscanner.presentation.adapters.base.DelegateItem

class ImageDelegateItem(private val value: ImageItem) : DelegateItem {

    override fun content(): Any = value

    override fun id(): Int = value.id

    override fun compareToOther(other: DelegateItem): Boolean {
        return (other as ImageDelegateItem).value == content()
    }

    override fun changePayloads(other: DelegateItem): Boolean {
        return false
    }
}
