package vn.icheck.android.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import java.io.Serializable

@Entity(tableName = "ward_table")
data class ICWard(
        @PrimaryKey @ColumnInfo(name = "id") val id: Int,
        @ColumnInfo(name = "name") val name: String,
        @ColumnInfo(name = "search_name") val searchName: String,
        @ColumnInfo(name = "district_id") val district_id: Int
) : Serializable