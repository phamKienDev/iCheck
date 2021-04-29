package vn.icheck.android.network.model.icklogin

import com.google.gson.annotations.SerializedName
import vn.icheck.android.network.models.ICSessionData

data class IckLoginResponse(

	@field:SerializedName("message")
	val msg: String? = null,

	@field:SerializedName("statusCode")
	val code: String? = null,

	@field:SerializedName("data")
	val data: ICSessionData? = null
)
