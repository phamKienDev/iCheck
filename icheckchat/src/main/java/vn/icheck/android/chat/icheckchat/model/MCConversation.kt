package vn.icheck.android.chat.icheckchat.model

import java.io.Serializable

class MCConversation : Serializable {

    var enableAlert: Boolean? = null

    var time: Long? = null

    var unreadCount: Long? = null

    var keyRoom: String? = null

    var key: String? = null

    var lastMessage: String? = null

    var targetUserName: String? = null

    var imageTargetUser: String? = null

    var isOnline: Boolean = false

    var members = mutableListOf<String>()

    var isNotification: Boolean = true

    var type: String = "user"

    var isVerified: Boolean = false

    var kycStatus: Long? = null
}