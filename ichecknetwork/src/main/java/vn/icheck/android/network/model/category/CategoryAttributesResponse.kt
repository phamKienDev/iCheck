package vn.icheck.android.network.model.category

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import kotlinx.android.parcel.RawValue

data class CategoryAttributesResponse(

		@field:SerializedName("data")
	val data: List<CategoryAttributesItem>? = null,

		@field:SerializedName("statusCode")
	val statusCode: String? = null
)

@Parcelize
data class OptionsItem(

	@field:SerializedName("id")
	val id: Int? = null,

	@field:SerializedName("value")
	val value: String? = null
):Parcelable {
	override fun toString(): String {
		return value ?: ""
	}
}

@Parcelize
data class CategoryAttributesItem(

		@field:SerializedName("code")
	val code: String? = null,

		@field:SerializedName("name")
	val name: String? = null,

		@field:SerializedName("id")
	val id: Int? = null,

		@field:SerializedName("type")
	val type: String? = null,

		@field:SerializedName("options")
	val options: MutableList<OptionsItem?>? = null,

		@field:SerializedName("required")
	val required: Boolean? = null,

		@field:SerializedName("value")
	val value: @RawValue Any? = null,

		@field:SerializedName("description")
	val description:String? = null
):Parcelable
