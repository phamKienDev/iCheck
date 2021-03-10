package vn.icheck.android.fragments.message.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ic_messages")
data class ICMessage(
        @PrimaryKey
        @ColumnInfo(name = "fb_id") val id: String,
        @ColumnInfo(name = "last_seen") var lastSeen:Long
){
    @ColumnInfo(name = "room_name") var roomName:String? = ""
    @ColumnInfo(name = "last_message") var lastMessage:String? = ""
    @ColumnInfo(name = "is_online") var isOnline:Boolean? = false
    @ColumnInfo(name = "unread_count") var unreadCount:Int = 0
    @ColumnInfo(name = "avatar") var avatar:String = ""
    @ColumnInfo(name = "user_id") var userId:Long = 0L
    @ColumnInfo(name ="message_type") var msgType:Int = MsgType.TYPE_USER_2_USER
    @ColumnInfo(name = "user_type") var userType:String? = "user"
    @ColumnInfo(name = "verified") var verified:Boolean? = false
}