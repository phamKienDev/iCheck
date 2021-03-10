package vn.icheck.android.network.models.campaign

import com.google.gson.annotations.SerializedName

data class CampaignResponse(

        @field:SerializedName("data")
	val data: CampaignData? = null,

        @field:SerializedName("statusCode")
	val statusCode: String? = null
)

data class CampaignItem(

	@field:SerializedName("image")
	val image: String? = null,

	@field:SerializedName("beginAt")
	val beginAt: String? = null,

	@field:SerializedName("endedAt")
	val endedAt: String? = null,

	@field:SerializedName("businessName")
	val businessName: String? = null,

	@field:SerializedName("logo")
	val logo: String? = null,

	@field:SerializedName("successNumber")
	val successNumber: Int? = null,

	@field:SerializedName("state")
	val state: Int? = null,

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("title")
	val title: String? = null
)

data class CampaignData(

	@field:SerializedName("count")
	val count: Int? = null,

	@field:SerializedName("rows")
	val rows: List<CampaignItem>? = null
)
