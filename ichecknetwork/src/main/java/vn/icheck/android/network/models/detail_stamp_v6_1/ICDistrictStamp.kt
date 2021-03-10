package vn.icheck.android.network.models.detail_stamp_v6_1

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ICDistrictStamp(

	@SerializedName("data")
	val data: DataDistrictStamp? = null,

	@SerializedName("error")
	val error: Boolean? = null,

	@SerializedName("status")
	val status: Int? = null
) : Serializable

data class DataDistrictStamp(

	@SerializedName("districts")
	val districts: MutableList<DistrictsItem>? = null
) : Serializable

data class DistrictsItem(

	@SerializedName("name")
	val name: String? = null,

	@SerializedName("id")
	val id: Int = 0,

	@SerializedName("city_id")
	val cityId: Int? = null,

	var searchKey: String = ""
) : Serializable
