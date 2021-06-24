package vn.icheck.android.chat.icheckchat.model

import com.google.gson.annotations.SerializedName
import vn.icheck.android.chat.icheckchat.R
import vn.icheck.android.chat.icheckchat.helper.ShareHelperChat
import vn.icheck.android.ichecklibs.util.getString

open class MCResponseCode {
    @SerializedName("statusCode") var statusCode: String = ""
    @SerializedName("code") var code: Int = 0
    @SerializedName("message") var message: String? = ShareHelperChat.getString(getString(R.string.error_default))
}