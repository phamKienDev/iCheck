package vn.icheck.android.chat.icheckchat.model

import java.io.Serializable

class MCDetailMessage : Serializable {

    var content: String? = null

    var link: String? = null

    var sticker: String? = null

    var type: String? = null

    var senderId: String? = null

    var time: Long? = null

    var userId: String? = null

    var avatarSender: String? = null

    var listMedia: MutableList<MCMedia>? = null

    var product: MCProductFirebase? = null

    var showTime: Boolean = false
}