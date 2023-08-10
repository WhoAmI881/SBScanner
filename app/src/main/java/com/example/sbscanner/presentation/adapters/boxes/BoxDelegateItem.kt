package com.example.sbscanner.presentation.adapters.boxes

import com.example.sbscanner.presentation.adapters.base.DelegateItem

class BoxDelegateItem(private val value: BoxItem) : DelegateItem {

    override fun content(): Any = value

    override fun id(): Int = value.id

    override fun compareToOther(other: DelegateItem): Boolean {
        return (other as BoxDelegateItem).value == content()
    }

    override fun changePayloads(other: DelegateItem): Boolean {
        return false
    }
}
