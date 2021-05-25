package vn.icheck.android.network.model.chat

import android.os.Parcelable
import androidx.annotation.StringDef
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import vn.icheck.android.network.base.SessionManager

@Retention(AnnotationRetention.SOURCE)
@StringDef(USER, PAGE)
annotation class ChatUserType

const val USER = "\"user\""
const val PAGE = "\"page\""
const val MEMBER = "\"member\""
const val ADMIN = "\"admin\""

@Retention(AnnotationRetention.SOURCE)
@StringDef(MEMBER, ADMIN)
annotation class ChatRoleType

@Parcelize
data class ChatMember(val id: Long?, @ChatUserType val type: String = USER, @ChatRoleType val role: String = MEMBER) : Parcelable


@Entity(tableName = "chat_conversation")
data class ChatConversation(  @PrimaryKey  val key:String){

    @ColumnInfo(name = "enable_alert")
    var enableAlert:Long = 0L

    @ColumnInfo(name = "key_room")
    var keyRoom = ""

    @ColumnInfo(name = "unread_count")
    var unreadCount = 0L

    @ColumnInfo(name = "last_activity")
    var time:Long? = System.currentTimeMillis()

    @Ignore
    val members = arrayListOf<String?>()

    @ColumnInfo(name = "last_message")
    var lastMessage = ""

    @Ignore
    var targetUser:ChatUser? = null

    @Ignore
    val listUser = arrayListOf<ChatMember>()

    @ColumnInfo(name = "image")
    var imageTargetUser:String = ""

    @ColumnInfo(name = "target_name")
    var targetUserName:String = ""

    fun getTargetUser():String? {
        val member = keyRoom.split("|")
        for (i in member) {
            if (i != "user-${SessionManager.session.user?.id}") {
                members.add(i.replace("-","|"))
                try {
                    listUser.add(ChatMember(i.replace("user|", "").toLong()))
                } catch (e: Exception) {
                }
            }
            try {
                listUser.add(ChatMember(i.replace("user|", "")?.toLong()))
            } catch (e: Exception) {
            }
        }
        return members.firstOrNull()
    }

    @ColumnInfo(name = "is_verify")
    var isVerify = false

    @ColumnInfo(name = "is_online")
    var isOnline = false

}