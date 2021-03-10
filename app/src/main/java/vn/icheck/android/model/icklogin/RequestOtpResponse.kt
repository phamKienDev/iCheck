package vn.icheck.android.model.icklogin

import com.google.gson.annotations.SerializedName

data class RequestOtpResponse(

	@field:SerializedName("data")
	val data: RequestOtpData? = null,

	@field:SerializedName("message")
	val message: String? = null,

	@field:SerializedName("statusCode")
	val statusCode: String? = null
)

data class RequestOtpData(

	@field:SerializedName("token")
	val token: String? = null
)
