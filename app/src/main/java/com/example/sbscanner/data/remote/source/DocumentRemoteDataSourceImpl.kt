package com.example.sbscanner.data.remote.source

import com.example.sbscanner.data.remote.retrofit.api.DocumentApi
import com.example.sbscanner.data.source.remote.DocumentRemoteDataSource

class DocumentRemoteDataSourceImpl(
    private val documentApi: DocumentApi
) : DocumentRemoteDataSource {
}
