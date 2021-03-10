package vn.icheck.android.model.privacy

import com.google.gson.annotations.SerializedName

data class UserPrivacyResponse(

	@field:SerializedName("data")
	val data: PrivacyData? = null,

	@field:SerializedName("statusCode")
	val statusCode: String? = null
)

data class PrivacyData(
		@field:SerializedName("rows")
		val data: List<PrivacyDataItem?>? = null
)

data class PrivacyDataItem(

	@field:SerializedName("privacyCode")
	val privacyCode: String? = null,

	@field:SerializedName("privacyElementName")
	val privacyElementName: String? = null,

	@field:SerializedName("privacyElementOrder")
	val privacyElementOrder: Int? = null,

	@field:SerializedName("privacyId")
	val privacyId: Int? = null,

	@field:SerializedName("privacyElementCode")
	val privacyElementCode: String? = null,

	@field:SerializedName("privacyOrder")
	val privacyOrder: Int? = null,

	@field:SerializedName("privacyName")
	val privacyName: String? = null,

	@field:SerializedName("privacyElementId")
	val privacyElementId: Int? = null,

	@field:SerializedName("privacyType")
	val privacyType: String? = null,

	@field:SerializedName("selected")
	val selected: Boolean? = null
)
