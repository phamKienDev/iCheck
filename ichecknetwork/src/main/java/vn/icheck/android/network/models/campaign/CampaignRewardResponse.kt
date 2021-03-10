package vn.icheck.android.network.models.campaign

import com.google.gson.annotations.SerializedName

data class CampaignRewardResponse(

	@field:SerializedName("data")
	val data: CampaignRewardData? = null,

	@field:SerializedName("statusCode")
	val statusCode: String? = null
)

data class CampaignRewardItem(

	@field:SerializedName("rewardTotal")
	val rewardTotal: Long? = null,

	@field:SerializedName("rewardValue")
	val rewardValue: Long? = null,

	@field:SerializedName("name")
	val name: String? = null,

	@field:SerializedName("businessName")
	val businessName: String? = null,

	@field:SerializedName("logo")
	val logo: String? = null,

	@field:SerializedName("image")
	val image: String? = null,

	@field:SerializedName("id")
	val id: String? = null,

	@field:SerializedName("title")
	val title: String? = null,

	@field:SerializedName("icoin")
	val icoin: Int? = null,

	@field:SerializedName("type")
	val type: Int? = null,

	@field:SerializedName("businessType")
	val businessType: Int? = null,

	@field:SerializedName("productId")
	val productId: Int? = null,

	@field:SerializedName("entityId")
	val entityId: Int? = null
)

data class CampaignRewardData(

	@field:SerializedName("rows")
	val rows: List<CampaignRewardItem>? = null
)
