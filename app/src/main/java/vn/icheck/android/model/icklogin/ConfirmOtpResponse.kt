package vn.icheck.android.model.icklogin

import com.google.gson.annotations.SerializedName

data class ConfirmOtpResponse(

        @field:SerializedName("data")
        val data: ConfirmOtpData? = null,

        @field:SerializedName("message")
        val message: String? = null,

        @field:SerializedName("statusCode")
        val statusCode: String? = null
)

data class ConfirmOtpData(

        @field:SerializedName("keepAlive")
        val keepAlive: Boolean? = null,

        @field:SerializedName("loginType")
        val loginType: Int? = null,

        @field:SerializedName("userType")
        val userType: Int? = null,

        @field:SerializedName("appUserId")
        val appUserId: Long? = null,

        @field:SerializedName("expiredTime")
        val expiredTime: Int? = null,

        @field:SerializedName("token")
        val token: String? = null,

        @field:SerializedName("firstLogin")
        val firstLogin:Boolean? = null,

        @field:SerializedName("firebaseToken")
        val firebaseToken:String,

        @field:SerializedName("responseCode")
        val responseCode:String? = null
)
