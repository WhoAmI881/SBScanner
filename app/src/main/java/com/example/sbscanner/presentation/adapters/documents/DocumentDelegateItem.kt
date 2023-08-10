package com.example.sbscanner.presentation.adapters.documents

import com.example.sbscanner.presentation.adapters.base.DelegateItem

class DocumentDelegateItem(private val value: DocumentItem) : DelegateItem {

    override fun content(): Any = value

    override fun id(): Int = value.id

    override fun compareToOther(other: DelegateItem): Boolean {
        return (other as DocumentDelegateItem).value == content()
    }

    override fun changePayloads(other: DelegateItem): Boolean {
        return false
    }
}
