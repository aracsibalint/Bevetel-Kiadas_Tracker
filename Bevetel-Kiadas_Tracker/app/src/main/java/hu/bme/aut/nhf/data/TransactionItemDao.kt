package hu.bme.aut.nhf.data

import androidx.room.*

@Dao
abstract class TransactionItemDao {
    @Query("SELECT * FROM transactionitem")
    abstract fun getAll(): List<TransactionItem>

    @Query("SELECT SUM(price) FROM transactionitem WHERE isIncome = :isIncome AND createdAt LIKE  :month || '%'")
    abstract fun getSumThisMonth(isIncome: Int, month: String): Int

    //0: bevetel
    //1: kiadas
    @Query("SELECT SUM(price) FROM transactionitem WHERE isIncome = :isIncome")
    abstract fun getSumPrice(isIncome: Int): Int

    @Query("SELECT SUM(price) FROM transactionitem")
    abstract fun getSumPrice(): Int

    @Insert
    abstract fun insert(transactionitems: TransactionItem): Long

    @Update
    abstract fun update(transactionitems: TransactionItem)

    @Delete
    abstract fun deleteItem(transactionitems: TransactionItem)
}