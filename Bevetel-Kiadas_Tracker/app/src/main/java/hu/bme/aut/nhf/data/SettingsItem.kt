package hu.bme.aut.nhf.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter

@Entity(tableName = "settingsitem")
data class SettingsItem(
    @ColumnInfo(name = "id") @PrimaryKey(autoGenerate = true) val id: Long?,
    @ColumnInfo(name = "maxSpendingPerMonth") val maxSpendingPerMonth: Int
) {

}