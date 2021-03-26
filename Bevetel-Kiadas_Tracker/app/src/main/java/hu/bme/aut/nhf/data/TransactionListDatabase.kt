package hu.bme.aut.nhf.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters

@Database(entities = arrayOf(TransactionItem::class, SettingsItem::class), version = 1)
@TypeConverters(value = [TransactionItem.Category::class, TransactionItem.IsIncome::class])
abstract class TransactionListDatabase : RoomDatabase() {
    abstract fun transactionItemDao(): TransactionItemDao
    abstract fun settingsItemDao(): SettingsItemDao
}