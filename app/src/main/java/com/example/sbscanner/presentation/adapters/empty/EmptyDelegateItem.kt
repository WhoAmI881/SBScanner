package com.example.sbscanner.presentation.adapters.empty

import com.example.sbscanner.presentation.adapters.base.DelegateItem

class EmptyDelegateItem(private val value: EmptyItem) : DelegateItem {

    override fun content(): Any = value

    override fun id(): Int = 0

    override fun compareToOther(other: DelegateItem): Boolean {
        return (other as EmptyDelegateItem).value == content()
    }

    override fun changePayloads(other: DelegateItem): Boolean {
        return false
    }
}
