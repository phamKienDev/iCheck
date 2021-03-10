package vn.icheck.android.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "my_friend_id_user")
data class ICMyFriendIdUser(
        @PrimaryKey @ColumnInfo(name = "id") val id: Long
)