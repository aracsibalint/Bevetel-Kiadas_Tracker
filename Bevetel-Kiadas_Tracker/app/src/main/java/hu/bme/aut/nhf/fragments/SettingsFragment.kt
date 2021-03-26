package hu.bme.aut.nhf.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import hu.bme.aut.nhf.MainActivity
import hu.bme.aut.nhf.R
import hu.bme.aut.nhf.data.SettingsItem
import hu.bme.aut.nhf.data.TransactionListDatabase
import kotlinx.android.synthetic.main.dialog_new_settings_item.view.*


class SettingsFragment : DialogFragment() {
    private lateinit var database: TransactionListDatabase
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        database = (activity as MainActivity?)?.getSettingsDatabase()!!

        var rootView = inflater.inflate(R.layout.dialog_new_settings_item, container, false)
        val maxSpendingPerMonthEditText = rootView.findViewById(R.id.settingsItemPriceEditText) as EditText
        var a = database.settingsItemDao().getMaxSpendingPerMonth()
        maxSpendingPerMonthEditText.setHint(a.toString())

        rootView.cancelButton.setOnClickListener{
            dismiss()
        }

        rootView.submitButton.setOnClickListener{
            val maxSpendingPerMonthEditText = rootView.findViewById(R.id.settingsItemPriceEditText) as EditText
            var maxSpendingPerMonthString = maxSpendingPerMonthEditText.getText().toString()
            var maxSpendingPerMonth = 0
            if(!maxSpendingPerMonthString.isEmpty()){
                maxSpendingPerMonth = Integer.parseInt(maxSpendingPerMonthString)
            }
            val si = SettingsItem(null, maxSpendingPerMonth)
            var asd = database.settingsItemDao().insert(si)
            var a = database.settingsItemDao()getMaxSpendingPerMonthByID(asd)
            dismiss()
        }

        // Inflate the layout for this fragment
        return rootView//inflater.inflate(R.layout.dialog_new_settings_item, container, false)
    }
}