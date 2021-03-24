package vn.icheck.android.chat.icheckchat.model

data class MCMessageEvent(val type: Type, val data: Any? = null) {

    enum class Type {
        UPDATE_DATA,
        BACK,
        BLOCK,
        ON_FINISH_ALL_CHAT,
        HIDE_KEYBOARD,
        SEND_RETRY_CHAT
    }
}