package vn.icheck.android.fragments.message.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import vn.icheck.android.fragments.message.MsgTimeHelper

class GroupMsgModel(val fbId:String, val time:Long):MsgModel {
    var name:String? = ""
    var members = arrayListOf<String>()
    var backgroundImage:String? = ""
    var admind:String? = ""
    var addMembers:Boolean? = null
    var logo:String? = ""
    var lastMsg:String? = ""
    val timeDisplay = MsgTimeHelper(time).getTime()
    var unreadCount = 0L

    override fun getType(): Int {
        return MsgType.TYPE_GROUP_MSG
    }

    override fun getFirebaseId(): String {
        return fbId
    }

    override fun getTimestamp(): Long {
        return time
    }
}