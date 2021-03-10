package vn.icheck.android.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "province_table")
data class ICProvince(
        @PrimaryKey @ColumnInfo(name = "id") val id: Int,
        @ColumnInfo(name = "name") val name: String,
        @ColumnInfo(name = "search_name") val searchName: String,
        @ColumnInfo(name = "country_id") val country_id: Int
) : Serializable {
    override fun toString(): String {
        return name
    }
}