package com.example.sbscanner.presentation.fragments.document.info

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import androidx.core.view.isVisible
import com.example.sbscanner.databinding.TemplateDocumentFormBinding
import com.example.sbscanner.domain.models.Document
import com.example.sbscanner.domain.utils.EMPTY_ID
import java.util.*

data class FormData(
    val docId: Int = EMPTY_ID,
    val barcode: String = "",
    val title: String = "",
    val date: String = "",
    val note: String = "",
    val isSimpleInventory: Boolean = false
)

fun FormData.toDomain() = Document(
    id = docId,
    barcode = barcode,
    title = title,
    date = date,
    note = note,
    isSimpleInventory = isSimpleInventory,
)

fun Document.toFormData() = FormData(
    docId = id,
    barcode = barcode,
    title = title,
    date = date,
    note = note,
    isSimpleInventory = isSimpleInventory,
)

fun TemplateDocumentFormBinding.setFormData(formData: FormData) {
    docBarcode.text = formData.barcode
    docTitle.setText(formData.title)
    docDate.setText(formData.date)
    docDate.addTextChangedListener(DateMask())
    docNote.setText(formData.note)
    isSimpleInventory.isChecked = formData.isSimpleInventory
    root.isVisible = true
}

fun TemplateDocumentFormBinding.getFormData() = FormData(
    title = docTitle.text.toString(),
    date = docDate.text.toString(),
    note = docNote.text.toString(),
    barcode = docBarcode.text.toString(),
    isSimpleInventory = isSimpleInventory.isChecked
)

fun TemplateDocumentFormBinding.openDatePicker(context: Context) {
    /*
    val c = Calendar.getInstance()

    val year = c.get(Calendar.YEAR)
    val month = c.get(Calendar.MONTH)
    val day = c.get(Calendar.DAY_OF_MONTH)

    val datePickerDialog = DatePickerDialog(context, { _, year, month, day ->
        docDate.setText(dateFormat(day, month, year))
    }, year, month, day)

    datePickerDialog.show()

     */
}

fun dateFormat(day: Int, month: Int, year: Int) =
    (day.toString() + "/" + (month + 1) + "/" + year)


class DateMask : TextWatcher {
    private var updatedText: String? = null
    private var editing = false
    override fun beforeTextChanged(
        charSequence: CharSequence,
        start: Int,
        before: Int,
        count: Int
    ) {
    }

    override fun onTextChanged(text: CharSequence, start: Int, before: Int, count: Int) {
        if (text.toString() == updatedText || editing) return
        var digits = text.toString().replace("\\D".toRegex(), "")
        val length = digits.length
        if (length <= MIN_LENGTH) {
            updatedText = digits
            return
        }
        if (length > MAX_LENGTH) {
            digits = digits.substring(0, MAX_LENGTH)
        }
        updatedText = if (length <= 4) {
            val month = digits.substring(0, 2)
            val day = digits.substring(2)
            java.lang.String.format(Locale.US, "%s/%s", month, day)
        } else {
            val month = digits.substring(0, 2)
            val day = digits.substring(2, 4)
            val year = digits.substring(4)
            java.lang.String.format(Locale.US, "%s/%s/%s", month, day, year)
        }
    }

    override fun afterTextChanged(editable: Editable) {
        if (editing) return
        editing = true
        editable.clear()
        editable.insert(0, updatedText)
        editing = false
    }

    companion object {
        private const val MAX_LENGTH = 8
        private const val MIN_LENGTH = 2
    }
}