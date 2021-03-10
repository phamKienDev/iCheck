package vn.icheck.android.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "owner_page")
data class ICOwnerPage(
        @PrimaryKey @ColumnInfo(name = "id") val id: Long
)