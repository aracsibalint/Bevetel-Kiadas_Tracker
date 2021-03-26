package hu.bme.aut.nhf.data

import androidx.room.*

@Dao
abstract class SettingsItemDao {
    @Query("SELECT maxSpendingPerMonth FROM settingsitem where id = (SELECT MAX(id) FROM settingsitem)")
    abstract fun getMaxSpendingPerMonth(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract fun insert(settingsitem: SettingsItem): Long

    @Query("INSERT INTO settingsitem VALUES(:id, :maxSpendingPrice)")
    abstract fun setMaxSpendingPerMonthInsert(id: Int, maxSpendingPrice: Int)

    @Update
    abstract fun update(settingsitem: SettingsItem)

    @Query("SELECT maxSpendingPerMonth FROM settingsitem where id = :id")
    abstract infix fun getMaxSpendingPerMonthByID(id: Long?): Int

    @Delete
    abstract fun deleteItem(settingsitem: SettingsItem)

    @Query("UPDATE settingsitem SET maxSpendingPerMonth = :maxSpendingPrice WHERE /*maxSpendingPerMonth = (SELECT MIN(maxSpendingPerMonth) FROM settingsitem)*/id = 1")
    abstract fun setMaxSpendingPerMonthUpdate(maxSpendingPrice: Int): Int
}