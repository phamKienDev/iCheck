package vn.icheck.android.model.firebase

import com.google.gson.annotations.SerializedName

data class LoginDeviceResponse(

	@field:SerializedName("deviceId")
	val deviceId: String? = null,

	@field:SerializedName("platform")
	val platform: String? = null,

	@field:SerializedName("deviceToken")
	val deviceToken: String? = null
)
