package vn.icheck.android.chat.icheckchat.model

import java.io.File
import java.io.Serializable

class MCDetailMessage : Serializable {

    var content: String? = null

    var link: String? = null

    var sticker: Any? = null

    var type: String? = null

    var senderId: String? = null

    var time: Long? = null

    var userId: String? = null

    var avatarSender: String? = null

    var listMedia: MutableList<MCMedia>? = null

    var listMediaFile: MutableList<File>? = null

    var product: MCProductFirebase? = null

    var showStatus: Int = 1 // -1: hiện, k được ẩn, 0: ẩn, 1: hiện, được ẩn

    var status: MCStatus? = MCStatus.SUCCESS

    var timeText: String? = null

    var messageId: String? = null
}