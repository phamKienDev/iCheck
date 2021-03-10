package vn.icheck.android.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "friend_invitation_me_user_id")
data class ICFriendInvitationMeUserId(
        @PrimaryKey @ColumnInfo(name = "id") val id: Long
)