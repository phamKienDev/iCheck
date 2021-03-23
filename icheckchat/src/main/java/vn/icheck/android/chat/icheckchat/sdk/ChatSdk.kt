package vn.icheck.android.chat.icheckchat.sdk

import vn.icheck.android.chat.icheckchat.base.ConstantChat
import vn.icheck.android.chat.icheckchat.helper.ShareHelperChat

object ChatSdk {

    private var listener: SdkChatListener? = null

    fun shareIntent(firebaseToken: String?, id: Long?, token: String?, deviceId: String?) {
        ShareHelperChat.clearData()

        ShareHelperChat.putString(ConstantChat.TOKEN_FIREBASE, firebaseToken)
        ShareHelperChat.putString(ConstantChat.TOKEN, token)
        ShareHelperChat.putString(ConstantChat.DEVICE_ID, deviceId)
        ShareHelperChat.putLong(ConstantChat.USER_ID, id ?: -1)
    }

    fun openActivity(schema: String) {
        if (schema.startsWith("icheck://") || schema.startsWith("http://") || schema.startsWith("https://")) {
            listener?.startActivity(schema)
        } else {
            listener?.startActivity("icheck://$schema")
        }
    }

    fun startFirebaseDynamicLinksActivity(listener: SdkChatListener) {
        this.listener = listener
    }

    interface SdkChatListener {
        fun startActivity(schema: String?)
    }
}