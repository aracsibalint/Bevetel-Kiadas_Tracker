package hu.bme.aut.nhf.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter

@Entity(tableName = "transactionitem")
data class TransactionItem(
    @ColumnInfo(name = "id") @PrimaryKey(autoGenerate = true) val id: Long?,
    @ColumnInfo(name = "description") val description: String,
    @ColumnInfo(name = "category") val category: Category,
    @ColumnInfo(name = "price") val price: Int,
    @ColumnInfo(name = "isIncome") val isIncome: Int,
    @ColumnInfo(name = "createdAt") var createdAt: String
) {
    enum class Category {
        //FOOD, ELECTRONIC, BOOK;
        ETEL, SZAMLA, FIZETES, AJANDEK, LAKAS, EGYEB, SZORAKOZAS, UTAZAS;
        companion object {
            @JvmStatic
            @TypeConverter
            fun getByOrdinal(ordinal: Int): Category? {
                var ret: Category? = null
                for (cat in values()) {
                    if (cat.ordinal == ordinal) {
                        ret = cat
                        break
                    }
                }
                return ret
            }

            @JvmStatic
            @TypeConverter
            fun toInt(category: Category): Int {
                return category.ordinal
            }
        }
    }

    enum class IsIncome {
        BEVETEL, KIADAS;
        companion object {
            @JvmStatic
            @TypeConverter
            fun getByOrdinal(ordinal: Int): IsIncome? {
                var ret: IsIncome? = null
                for (cat in values()) {
                    if (cat.ordinal == ordinal) {
                        ret = cat
                        break
                    }
                }
                return ret
            }

            @JvmStatic
            @TypeConverter
            fun toInt(isIncome: IsIncome): Int {
                return isIncome.ordinal
            }
        }
    }
}