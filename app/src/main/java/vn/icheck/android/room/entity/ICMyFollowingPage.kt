package vn.icheck.android.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "my_following_page")
data class ICMyFollowingPage(
        @PrimaryKey @ColumnInfo(name = "id") val id: Long
)