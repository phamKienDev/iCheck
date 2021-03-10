package vn.icheck.android.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "me_follow_user")
data class ICMeFollowUser(
        @PrimaryKey @ColumnInfo(name = "id") val id: Long
)