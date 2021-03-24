package vn.icheck.android.chat.icheckchat.model

import com.google.gson.annotations.SerializedName
import vn.icheck.android.chat.icheckchat.R
import vn.icheck.android.chat.icheckchat.helper.ShareHelperChat

open class MCResponseCode {
    @SerializedName("statusCode") var statusCode: String = ""
    @SerializedName("code") var code: Int = 0
    @SerializedName("message") var message: String? = ShareHelperChat.getString(R.string.error_default)
}