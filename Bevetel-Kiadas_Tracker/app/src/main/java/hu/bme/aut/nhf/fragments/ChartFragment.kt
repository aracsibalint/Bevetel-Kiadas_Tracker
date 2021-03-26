package hu.bme.aut.nhf.fragments

//import androidx.test.core.app.ApplicationProvider.getApplicationContext

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import hu.bme.aut.nhf.MainActivity
import hu.bme.aut.nhf.R
import hu.bme.aut.nhf.data.TransactionItem
import hu.bme.aut.nhf.data.TransactionListDatabase
import kotlinx.android.synthetic.main.fragment_chart.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.thread


class ChartFragment : Fragment() {
    //private lateinit var adapter: TransactionAdapter
    private lateinit var database: TransactionListDatabase

    companion object {
        fun newInstance() = ChartFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_chart, container, false)

        var pieChart = view.findViewById<View>(R.id.ChartPrices) as PieChart
        database = (activity as MainActivity?)?.getDatabase()!!
        Log.d("SUMPRICE", (activity as MainActivity?)?.getSumSpendingPrice()?.toString()!!)

        val entries = listOf(
            PieEntry((activity as MainActivity?)?.getSumSpendingPrice()?.toFloat()!!, "kiadas"),
            PieEntry((activity as MainActivity?)?.getsumIncomePrice()?.toFloat()!!, "bevetel")
        )

        val dataSet = PieDataSet(entries, "Kiadas/Bevetel arany")
        dataSet.colors = ColorTemplate.MATERIAL_COLORS.toList()

        val data = PieData(dataSet)
        pieChart.data = data
        pieChart.invalidate()

        return view
    }
}

