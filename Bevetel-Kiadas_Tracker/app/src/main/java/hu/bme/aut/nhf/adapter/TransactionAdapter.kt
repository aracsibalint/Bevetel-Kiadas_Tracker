package hu.bme.aut.nhf.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.DrawableRes
import androidx.recyclerview.widget.RecyclerView
import hu.bme.aut.nhf.R
import hu.bme.aut.nhf.data.SettingsItem
import hu.bme.aut.nhf.data.TransactionItem
import kotlinx.android.synthetic.main.item_transaction_list.view.*

class TransactionAdapter(private val listener: TransactionItemClickListener, private val removeListener: TransactionItemRemovingDialogListener) :
    RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {

    private val items = mutableListOf<TransactionItem>()
    private var maxSpendingPerMonth = 0

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TransactionViewHolder {
        val itemView: View = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.item_transaction_list, parent, false)
        return TransactionViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TransactionViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    interface TransactionItemClickListener {
        fun onItemChanged(item: TransactionItem)
    }
    interface TransactionItemRemovingDialogListener {
        fun onTransactionItemRemoved(item: TransactionItem)
    }

    fun getItems(): List<TransactionItem>{
        return items
    }

    inner class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        private lateinit var transactionItem: TransactionItem

        fun bind(item: TransactionItem) {
            transactionItem = item

            itemView.TransactionItemIconImageView.setImageResource(getImageResource(transactionItem.isIncome))
            itemView.TransactionItemcreatedAtTextView.text = transactionItem.createdAt
            itemView.TransactionItemDescriptionTextView.text = transactionItem.description
            itemView.TransactionItemCategoryTextView.text = transactionItem.category.name
            itemView.TransactionItemPriceTextView.text = "${transactionItem.price} Ft"
            itemView.TransactionItemRemoveButton.setOnClickListener {
                removeItem(transactionItem)
                removeListener.onTransactionItemRemoved((transactionItem))
            }
        }

        @DrawableRes
        private fun getImageResource(isIncome: Int) = when (isIncome) {
            0 -> R.drawable.ic_bevetel
            else -> R.drawable.ic_kiadas
        }
    }

    fun addItem(transactionItem: TransactionItem) {
        Log.d("TAG", "addItem")
        items.add(transactionItem)
        notifyItemInserted(items.size - 1)
    }

    fun update(transactionItems: List<TransactionItem>) {
        items.clear()
        items.addAll(transactionItems)
        notifyDataSetChanged()
    }

    fun removeItem(transactionItem: TransactionItem) {
        items.remove(transactionItem)
        notifyDataSetChanged()
    }

    fun addItem(item: SettingsItem) {
        maxSpendingPerMonth = item.maxSpendingPerMonth
    }

    fun update(settingsItem: SettingsItem) {
        maxSpendingPerMonth = settingsItem.maxSpendingPerMonth
        notifyDataSetChanged()
    }
}