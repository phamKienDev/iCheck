package vn.icheck.android.network.models

import com.google.gson.annotations.SerializedName

data class ICSessionData (
        @SerializedName("token_type") var tokenType: String? = null,
        @SerializedName("expires_in") var expiresIn: Long = 0,
        @SerializedName("refresh_token") var refreshToken: String? = null,
        @SerializedName("firebaseToken") var firebaseToken: String? = null,
        @SerializedName("user_data") var user: ICUser? = null,
        // V2
        @SerializedName("token") var token: String? = null,
        @SerializedName("expiredTime") var expiredTime: Long? = null,
        @SerializedName("type") var type: Int? = null,
        @SerializedName("loginType") var loginType: Int? = null,
        @SerializedName("keepAlive") var keepAlive: Boolean? = null,
        @SerializedName("deviceToken") var deviceToken:String? = null,
        @SerializedName("userType") var userType:Int? = null,
        @SerializedName("uploadKey") var uploadKey:String? = null,
        @SerializedName("appUserId") var appUserId:Long? = null
)