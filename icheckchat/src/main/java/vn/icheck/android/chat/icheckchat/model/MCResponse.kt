package vn.icheck.android.chat.icheckchat.model

import com.google.gson.annotations.SerializedName

class MCResponse<T>(
        @SerializedName("data") val data: T?
) : MCResponseCode()