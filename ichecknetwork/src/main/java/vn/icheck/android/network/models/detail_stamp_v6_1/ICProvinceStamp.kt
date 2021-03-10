package vn.icheck.android.network.models.detail_stamp_v6_1

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ICProvinceStamp(

	@SerializedName("data")
	val data: Data? = null,

	@SerializedName("error")
	val error: Boolean? = null,

	@SerializedName("status")
	val status: Int? = null
) : Serializable

data class Data(

	@SerializedName("cities")
	val cities: MutableList<CitiesItem>? = null
) : Serializable

data class CitiesItem(

	@SerializedName("code")
	val code: String? = null,

	@SerializedName("province_id")
	val provinceId: Any? = null,

	@SerializedName("name")
	val name: String? = null,

	@SerializedName("id")
	val id: Int = 0,

	@SerializedName("phone_code")
	val phoneCode: String? = null,

	var searchKey: String = ""
) : Serializable
