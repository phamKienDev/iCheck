package vn.icheck.android.activities.chat.v2.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "ic_chat_messages")
data class ICChatMessage(
        @PrimaryKey
        @ColumnInfo(name = "fbc_id")
        val id: String,
        @ColumnInfo(name = "sent_time")
        val sentTime: Long
) {
    @ColumnInfo(name = "chat_msg_type")
    var chatMsgType: Int = ChatMsgType.TYPE_ERROR
    @ColumnInfo(name = "text_messsage")
    var textMessage: String? = ""
    @ColumnInfo(name = "product_id")
    var productBarcode: String? = ""
    @ColumnInfo(name = "image_msg")
    var imageMsg: String? = ""
    @ColumnInfo(name = "send_by_user")
    var sendByUser: Long? = 0L
    @ColumnInfo(name = "product_image")
    var productImg: String? = ""
    @ColumnInfo(name = "product_name")
    var productName: String? = ""
    @ColumnInfo(name = "product_price")
    var productPrice: Long? = 0L
    @ColumnInfo(name = "user_sent_avatar")
    var userSentAvatar:String? = ""
    @ColumnInfo(name ="user_type")
    var userType:String? = "user"
    @ColumnInfo(name ="show_avatar")
    var showAvatar = true

    /**
     * Default 1 = SENDING
     * 2 = SUCCESS
     * 3 = ERROR
     */
    @ColumnInfo(name = "state_send")
    var stateSendMessage = 2
}