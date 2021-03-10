package vn.icheck.android.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "my_friend_invitation_user_id")
data class ICMyFriendInvitationUserId(
        @PrimaryKey @ColumnInfo(name = "id") val id: Long
)