package hu.bme.aut.nhf

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.room.Room
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import hu.bme.aut.nhf.adapter.TransactionAdapter
import hu.bme.aut.nhf.data.TransactionItem
import hu.bme.aut.nhf.data.TransactionListDatabase
import hu.bme.aut.nhf.fragments.*
import hu.bme.aut.nhf.fragments.NewTransactionItemDialogFragment
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import kotlinx.android.synthetic.main.fragment_chart.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.concurrent.thread
import kotlinx.coroutines.*


class MainActivity : AppCompatActivity(), TransactionAdapter.TransactionItemClickListener, TransactionAdapter.TransactionItemRemovingDialogListener, CoroutineScope by MainScope(), NewTransactionItemDialogFragment.NewTransactionItemDialogListener{
    private lateinit var recyclerView : RecyclerView
    private lateinit var adapter: TransactionAdapter
    private lateinit var database: TransactionListDatabase
    private lateinit var databaseSettings: TransactionListDatabase

    private var sumSpendingPrice = 0
    private var sumIncomeThisMonth = 0
    private var sumSpendingThisMonth = 0
    private var sumIncomePrice = 0
    private var maxSpendingPerMonth = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.fragment_chart)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)

        fab.setOnClickListener{
            NewTransactionItemDialogFragment().show(
                supportFragmentManager,
                NewTransactionItemDialogFragment.TAG
            )
        }
        database = Room.databaseBuilder(
            applicationContext,
            TransactionListDatabase::class.java,
            "transactionitem"
        ).allowMainThreadQueries().build()
        setIncomeSpend()

        databaseSettings = Room.databaseBuilder(
            applicationContext,
            TransactionListDatabase::class.java,
            "settingsitem"
        ).allowMainThreadQueries().build()

        initRecyclerView()

        // Initially display the first fragment in main activity
        replaceFragment(ListFragment())

        // Drawer toggle click listener
        val toggle: ActionBarDrawerToggle = object : ActionBarDrawerToggle(
            this, drawer, toolbar,
            R.string.drawer_open, R.string.drawer_close
        ) {
            override fun onDrawerOpened(drawerView: View) {
                super.onDrawerOpened(drawerView)
            }

            override fun onDrawerClosed(drawerView: View) {
                super.onDrawerClosed(drawerView)
            }
        }

        // Configure drawer layout
        toggle.isDrawerIndicatorEnabled = true
        toggle.syncState()
        drawer.addDrawerListener(toggle)


        // Navigation view item click listener
        nav_view.setNavigationItemSelectedListener {
            drawer.closeDrawer(GravityCompat.START)
            when (it.itemId) {
                R.id.nav_settings -> {
                    replaceFragment(SettingsFragment())
                    Log.d("TAG", "settings")
                    true
                }
                R.id.nav_list -> {
                    replaceFragment(ListFragment())
                    Log.d("TAG", "list")
                    true
                }
                R.id.nav_chart -> {
                    replaceFragment(ChartFragment())
                    Log.d("TAG", "chart")
                    true
                }
                R.id.nav_chart2 -> {
                    replaceFragment(ChartFragment2())
                    Log.d("TAG", "chart2")
                    true
                }
                else -> false
            }
        }
    }

    private fun initRecyclerView() {
        recyclerView = MainRecyclerView
        adapter = TransactionAdapter(this, this)
        loadItemsInBackground()
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
    }

    private fun loadItemsInBackground() =launch{
        /*thread {
            val items = database.transactionItemDao().getAll()
            runOnUiThread {
                adapter.update(items)
            }

            sumSpendingThisMonth = database.transactionItemDao().getSumThisMonth(1, getThisMonth().toString())
            setIncomeSpend()
            setSettings()
            maxSpendingPerMonth = databaseSettings.settingsItemDao().getMaxSpendingPerMonth()
        }*/
        val items = withContext(Dispatchers.IO) {
            database.transactionItemDao().getAll()
        }
        adapter.update(items)
    }

    override fun onItemChanged(item: TransactionItem) {
        /*thread {
            database.transactionItemDao().update(item)
            sumSpendingThisMonth = database.transactionItemDao().getSumThisMonth(1, getThisMonth().toString())
            setIncomeSpend()
            setSettings()
        }*/
        updateItemInBackgound(item)
    }

    private fun updateItemInBackgound(item: TransactionItem) = launch{
        withContext(Dispatchers.IO) {
            database.transactionItemDao().update(item)
        }
    }

    override fun onTransactionItemCreated(newItem: TransactionItem) {
        /*thread {
            val newId = database.transactionItemDao().insert(newItem)
            val newTransactionItem = newItem.copy(
                id = newId
            )
            runOnUiThread {
                adapter.addItem(newTransactionItem)
            }

            setIncomeSpend()
            setSettings()

            sumSpendingThisMonth = database.transactionItemDao().getSumThisMonth(1, getThisMonth().toString())
            maxSpendingPerMonth = databaseSettings.settingsItemDao().getMaxSpendingPerMonth()
            if(getsumSpendingThisMonth() > maxSpendingPerMonth && maxSpendingPerMonth!=0){
                var diff = getsumSpendingThisMonth()-maxSpendingPerMonth
                runOnUiThread {
                    Toast.makeText(applicationContext, "A havi keretet $diff forinttal lepted tul.", Toast.LENGTH_LONG).show()
                }
            }
        }*/
        addItemInBackground(newItem)
    }

    private fun addItemInBackground(newItem: TransactionItem) = launch {
        withContext(Dispatchers.IO) {
            database.transactionItemDao().insert(newItem)
        }
        adapter.addItem(newItem)

        setIncomeSpend()
        if(newItem.isIncome == 1){
            maxSpendingPerMonth = databaseSettings.settingsItemDao().getMaxSpendingPerMonth()
            if(getsumSpendingThisMonth() > maxSpendingPerMonth && maxSpendingPerMonth!=0){
                var diff = getsumSpendingThisMonth()-maxSpendingPerMonth
                runOnUiThread {
                    Toast.makeText(applicationContext, "A havi keretet $diff forinttal lepted tul.", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    private fun setIncomeSpend() {
        sumSpendingPrice = database.transactionItemDao().getSumPrice(1)
        sumIncomePrice = database.transactionItemDao().getSumPrice(0)
        sumIncomeThisMonth = database.transactionItemDao()
            .getSumThisMonth(0, getThisMonth().toString())
        sumSpendingThisMonth = database.transactionItemDao()
            .getSumThisMonth(1, getThisMonth().toString())
    }

    private fun setSettings() {
        maxSpendingPerMonth = databaseSettings.settingsItemDao().getMaxSpendingPerMonth()
    }

    fun getDatabase() : TransactionListDatabase{
        return database
    }

    fun getSettingsDatabase() : TransactionListDatabase{
        return databaseSettings
    }

    private fun getThisMonth(): String {
        var month = getYear()+"-"+getDate(System.currentTimeMillis(), "MM")
        return month
    }

    private fun getYear(): String{
        var year = getDate(System.currentTimeMillis(), "yyyy")
        return year
    }

    private fun getDate(milliSeconds: Long, dateFormat: String): String {
        // Create a DateFormatter object for displaying date in specified format.
        val formatter = SimpleDateFormat(dateFormat)

        // Create a calendar object that will convert the date and time value in milliseconds to date.
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = milliSeconds
        return formatter.format(calendar.time)
    }

    fun getSumSpendingPrice() : Int{
        return sumSpendingPrice
    }

    fun getsumSpendingThisMonth() : Int{
        sumSpendingThisMonth = database.transactionItemDao().getSumThisMonth(1, getThisMonth().toString())
        return sumSpendingThisMonth
    }

    fun getsumIncomePrice() : Int{
        return sumIncomePrice
    }

    fun removeItem(transactionItem: TransactionItem){
        database.transactionItemDao().deleteItem(transactionItem)
    }

    override fun onTransactionItemRemoved(item: TransactionItem) {
        removeItemInBackground(item)
    }

    private fun removeItemInBackground(item: TransactionItem) = launch {
        withContext(Dispatchers.IO) {
            database.transactionItemDao().deleteItem(item)
        }
        adapter.removeItem(item)
        setIncomeSpend()
    }

    /*override fun onTransactionItemRemoved(item: TransactionItem) {
        removeItemInBackground(item)
    }

    private fun removeItemInBackground(item: TransactionItem) = launch {
        withContext(Dispatchers.IO) {
            database.transactionItemDao().deleteItem(item)
        }
        adapter.removeItem(item)
    }*/
    fun getAdapter(): TransactionAdapter{
        return adapter
    }
}

// Extension function to replace fragment
fun AppCompatActivity.replaceFragment(fragment: Fragment) {
    val fragmentManager = supportFragmentManager
    val transaction = fragmentManager.beginTransaction()
    transaction.replace(R.id.host, fragment)
    transaction.addToBackStack(null)
    transaction.commit()
}