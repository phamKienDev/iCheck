package vn.icheck.android.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "search_table")
data class ICSearch(
        @PrimaryKey @ColumnInfo(name = "key") val key: String
)