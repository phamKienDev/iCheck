package vn.icheck.android.model.location

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

data class CityResponse(

	@field:SerializedName("data")
	val data: CityData? = null,

	@field:SerializedName("statusCode")
	val statusCode: String? = null
)

data class CityData(

	@field:SerializedName("count")
	val count: Int? = null,

	@field:SerializedName("rows")
	val rows: List<CityItem?>? = null
)

@Parcelize
data class CityItem(

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("id")
	val id: Int? = null
):Parcelable
