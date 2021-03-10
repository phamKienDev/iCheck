package vn.icheck.android.fragments.message.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import vn.icheck.android.fragments.message.MsgTimeHelper
import vn.icheck.android.network.base.SessionManager
import vn.icheck.android.network.models.ICUserId

class UserToUserModel(val fbId:String, val fbTimestamp:Long):MsgModel {

    var icUserId:ICUserId? = null
    val timeDisplay = MsgTimeHelper(fbTimestamp).getTime()
    var message:String? = ""
    var isOnline = false
    var unreadCount = 0L
    var id:Long = 0L

    init {
        try {
            val uid =  SessionManager.session.user?.id
            id = fbId.replace("i-$uid","").replace("|", "").replace("i-", "").toLong()
        } catch (e: Exception) {
        }
    }

    override fun getType(): Int {
        return MsgType.TYPE_USER_2_USER
    }

    override fun getFirebaseId(): String {
        return fbId
    }

    override fun getTimestamp(): Long {
        return fbTimestamp
    }
}