package vn.icheck.android.network.model.icklogin

import com.google.gson.annotations.SerializedName

data class IckLoginFacebookResponse(

		@field:SerializedName("data")
	val data: FacebookResponseData? = null,

		@field:SerializedName("message")
	val message: String? = null,

		@field:SerializedName("statusCode")
	val statusCode: String? = null
)

data class FacebookResponseData(
		@field:SerializedName("token")
		var token:String? = null
)
