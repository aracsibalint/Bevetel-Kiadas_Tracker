package hu.bme.aut.nhf.fragments

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Spinner
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import hu.bme.aut.nhf.R
import hu.bme.aut.nhf.data.TransactionItem
import java.text.SimpleDateFormat
import java.util.*
import kotlinx.coroutines.NonCancellable.cancel

class NewTransactionItemDialogFragment : DialogFragment() {
    private lateinit var createdAtEditText: EditText
    private lateinit var descriptionEditText: EditText
    private lateinit var priceEditText: EditText
    private lateinit var categorySpinner: Spinner
    private lateinit var isIncomeSpinner: Spinner

    interface NewTransactionItemDialogListener {
        fun onTransactionItemCreated(newItem: TransactionItem)
    }

    private lateinit var listener: NewTransactionItemDialogListener

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as? NewTransactionItemDialogListener
            ?: throw RuntimeException("Activity must implement the NewTransactionItemDialogListener interface!")
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext())
            .setTitle(R.string.new_transaction_item)
            .setView(getContentView())
            .setPositiveButton(R.string.ok) { dialogInterface, i ->
                if (isValid()) {
                    listener.onTransactionItemCreated(getTransactionItem())
                }
            }
            .setNegativeButton(R.string.cancel, null)
            .create()
    }

    private fun isValid() = priceEditText.text.isNotEmpty()

    fun getDate(milliSeconds: Long, dateFormat: String): String {
        // Create a DateFormatter object for displaying date in specified format.
        val formatter = SimpleDateFormat(dateFormat)

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = milliSeconds
        return formatter.format(calendar.time)
    }

    private fun getTransactionItem() = TransactionItem (
        id = null,
        createdAt = getDate(System.currentTimeMillis(), "yyyy-MM hh:mm:ss"),
        description = descriptionEditText.text.toString(),
        price = try {
            priceEditText.text.toString().toInt()
        } catch (e: java.lang.NumberFormatException) {
            0
        },
        category = TransactionItem.Category.getByOrdinal(categorySpinner.selectedItemPosition)
            ?: TransactionItem.Category.ETEL,
        isIncome = isIncomeSpinner.selectedItemPosition
    )

    private fun getContentView(): View {
        val contentView =
            LayoutInflater.from(context).inflate(R.layout.dialog_new_transaction_item, null)
        //createdAtEditText = contentView.findViewById(R.id.transactionItemcreatedAtEditText)
        descriptionEditText = contentView.findViewById(R.id.transactionItemDescriptionEditText)
        priceEditText = contentView.findViewById(R.id.transactionItemPriceEditText)
        isIncomeSpinner = contentView.findViewById(R.id.transactionItemIsIncomeSpinner)
        categorySpinner = contentView.findViewById(R.id.transactionItemCategorySpinner)
        categorySpinner.setAdapter(
            ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                resources.getStringArray(R.array.category_items)
            )
        )
        isIncomeSpinner.setAdapter(
            ArrayAdapter(
                requireContext(),
                android.R.layout.simple_spinner_dropdown_item,
                resources.getStringArray(R.array.is_income_items)
            )
        )
        return contentView
    }

    companion object {
        const val TAG = "NewTransactionItemDialogFragment"
    }
}