package vn.icheck.android.chat.icheckchat.model

data class MCMessageEvent(val type: Type, val data: Any? = null) {

    enum class Type {
        UPDATE_DATA,
        BACK,
        BLOCK,
        ON_FINISH_ALL_CHAT,
        SEND_RETRY_CHAT,
        IS_SCROLL_MEDIA
    }
}