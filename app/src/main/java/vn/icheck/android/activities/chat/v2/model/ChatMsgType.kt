package vn.icheck.android.activities.chat.v2.model

import androidx.multidex.BuildConfig

object ChatMsgType {
    val customerServiceId = if (BuildConfig.BUILD_TYPE == "release") {
        102328L
    } else {
        100790L
    }
    const val TYPE_INCOMING_TEXT = 1
    const val TYPE_OUTGOING_TEXT = 2
    const val TYPE_INCOMING_IMG = 3
    const val TYPE_OUTGOING_IMG = 4
    const val TYPE_INCOMING_PRODUCT = 5
    const val TYPE_OUTGOING_PRODUCTT = 6
    const val TYPE_ERROR = 7
    const val TYPE_CHAT_BOT_HEADER = 8
    const val TYPE_CHAT_BOT_QA = 9
    const val TYPE_SYSTEM_MSG = 10
    const val TYPE_FIRST_BOT = 11
}