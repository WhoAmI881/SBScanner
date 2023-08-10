package com.example.sbscanner.data.remote.source

import com.example.sbscanner.data.remote.retrofit.api.TaskApi
import com.example.sbscanner.data.remote.retrofit.getResponse
import com.example.sbscanner.data.remote.retrofit.models.Form
import com.example.sbscanner.data.remote.retrofit.safeApiCall
import com.example.sbscanner.data.source.remote.TaskRemoteDataSource
import com.example.sbscanner.domain.models.Task
import kotlinx.coroutines.Dispatchers

class TaskRemoteDataSourceImpl(
    private val taskApi: TaskApi
) : TaskRemoteDataSource {

    override suspend fun sendTask(task: Task) = safeApiCall(Dispatchers.IO) {
        val form = Form(GET_SESSION.format(task.userId, task.barcode))
        taskApi.send(form).getResponse()
    }

    companion object {
        private const val GET_SESSION =
            "<data><connect_para><dt>12.06.2023 09:07:03</dt><appl_name>Delice_Invent_Mobile</appl_name><proc_name>da_inventory_mobile_sp</proc_name></connect_para><proc_para><start>1</start>" +
                    "<action>GET_SESSION</action>" +
                    "<user_barcode>%s</user_barcode>" +
                    "<task_barcode>%s</task_barcode>" +
                    "</proc_para></data>"
    }
}
