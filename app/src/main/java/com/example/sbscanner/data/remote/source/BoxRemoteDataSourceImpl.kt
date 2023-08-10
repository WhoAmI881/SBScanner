package com.example.sbscanner.data.remote.source

import com.example.sbscanner.data.remote.retrofit.api.BoxApi
import com.example.sbscanner.data.source.remote.BoxRemoteDataSource

class BoxRemoteDataSourceImpl(
    private val boxApi: BoxApi
) : BoxRemoteDataSource {
}
