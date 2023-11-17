package com.example.sbscanner.data.remote.source

import android.util.Base64
import com.example.sbscanner.data.remote.retrofit.api.ImageApi
import com.example.sbscanner.data.remote.retrofit.getResponse
import com.example.sbscanner.data.remote.retrofit.models.Form
import com.example.sbscanner.data.remote.retrofit.safeApiCall
import com.example.sbscanner.data.source.remote.ImageRemoteDataSource
import com.example.sbscanner.domain.models.SendImageForm
import kotlinx.coroutines.Dispatchers

class ImageRemoteDataSourceImpl(
    private val imageApi: ImageApi
) : ImageRemoteDataSource {

    override suspend fun sendImage(form: SendImageForm, bytes: ByteArray) =
        safeApiCall(Dispatchers.IO) {
            val data = Form(
                SAVE.format(
                    form.sessionId,
                    form.box.barcode,
                    form.box.dateCreate,
                    form.document.barcode,
                    form.document.title,
                    form.document.date,
                    form.document.note,
                    if (form.document.isSimpleInventory) "1" else "0",
                    form.document.dateCreate,
                    form.image.id,
                    Base64.encodeToString(bytes, Base64.DEFAULT),
                    form.image.dateCreate,
                    if (form.isLastImgInBox) "1" else "0",
                )
            )

            imageApi.send(data).getResponse()
        }

    companion object {

        private const val SAVE =
            "<data><connect_para><appl_name>Delice_Invent_Mobile</appl_name><proc_name>da_inventory_mobile_sp</proc_name></connect_para><proc_para><start>1</start>\n" +
                    "<action>SAVE</action>\n" +
                    "<id_ss_mobile>%s</id_ss_mobile>\n" +
                    "<box_barcode>%s</box_barcode>\n" +
                    "<box_start>%s</box_start>\n" +
                    "<doc_barcode>%s</doc_barcode>\n" +
                    "<doc_name>%s</doc_name>\n" +
                    "<doc_dt>%s</doc_dt>\n" +
                    "<doc_comment>%s</doc_comment>\n" +
                    "<doc_type>%s</doc_type>\n" +
                    "<doc_start>%s</doc_start>\n" +
                    "<id_doc_photo>%s</id_doc_photo>\n" +
                    "<doc_photo>%s</doc_photo>\n" +
                    "<doc_photo_start>%s</doc_photo_start>\n" +
                    "<box_final>%s</box_final>\n" +
                    "</proc_para></data>"
    }
}
