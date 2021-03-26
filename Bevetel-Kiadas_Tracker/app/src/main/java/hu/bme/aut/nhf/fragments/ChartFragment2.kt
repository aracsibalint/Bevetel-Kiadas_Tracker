package hu.bme.aut.nhf.fragments

import android.os.Bundle
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
import java.util.*

class ChartFragment2 : Fragment() {
    private lateinit var database: TransactionListDatabase

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view: View = inflater.inflate(R.layout.fragment_chart, container, false)
        //items = adapter!!.getItems().toMutableList()

        var pieChart = view.findViewById<View>(R.id.ChartPrices) as PieChart
        database = (activity as MainActivity?)?.getDatabase()!!
        //Log.d("SUMPRICE", (activity as MainActivity?)?.getSumSpendingPrice()?.toString()!!)
        val items = database.transactionItemDao().getAll()
        //var spendingPerCategory = mutableListOf<Int>()
        val spendingPerCategory = IntArray(8)

        var i = 0
        while(i<items.size) {
            if (items[i].isIncome == 1){
                spendingPerCategory[items.get(i).category.ordinal] += items[i].price
            }
            i++
        }

        val entries = mutableListOf<PieEntry>()
        i = 0
        while(i<spendingPerCategory.size) {
            if (spendingPerCategory[i] != 0){
                entries.add( PieEntry(spendingPerCategory[i].toFloat(), TransactionItem.Category.values()[i].toString()) )
            }
            i++
        }

        val dataSet = PieDataSet(entries, "Kiadas Kategorizalva")
        dataSet.colors = ColorTemplate.COLORFUL_COLORS.toList() //ColorTemplate.MATERIAL_COLORS.toList()

        val data = PieData(dataSet)
        pieChart.data = data
        pieChart.animateY(1000)
        pieChart.invalidate()

        return view
    }
}