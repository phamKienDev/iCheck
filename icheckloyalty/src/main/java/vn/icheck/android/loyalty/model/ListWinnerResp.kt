package vn.icheck.android.loyalty.model

import com.google.gson.annotations.SerializedName

data class ListWinnerResp(

        @field:SerializedName("data")
        val data: ListWinnerData? = null,

        @field:SerializedName("count")
        val count: Int? = null,

        @field:SerializedName("rows")
        val rows: List<ListWinnerRowsItem?>? = null,

        @field:SerializedName("statusCode")
        val statusCode: Int? = null,

        @field:SerializedName("status")
        val status: String? = null
)

data class ListWinnerRowsItem(

        @field:SerializedName("status_title")
        val statusTitle: String? = null,

        @field:SerializedName("code")
        val code: Any? = null,

        @field:SerializedName("status_time_title")
        val statusTimeTitle: String? = null,

        @field:SerializedName("created_at")
        val createdAt: String? = null,

        @field:SerializedName("lon")
        val lon: String? = null,

        @field:SerializedName("full_address")
        val fullAddress: String? = null,

        @field:SerializedName("geo")
        val geo: Any? = null,

        @field:SerializedName("district_name")
        val districtName: Any? = null,

        @field:SerializedName("city_name")
        val cityName: Any? = null,

        @field:SerializedName("ward_name")
        val wardName: Any? = null,

        @field:SerializedName("updated_at")
        val updatedAt: String? = null,

        @field:SerializedName("id")
        val id: Int? = null,

        @field:SerializedName("business_owner_id")
        val businessOwnerId: Int? = null,

        @field:SerializedName("status_time")
        val statusTime: String? = null,

        @field:SerializedName("campaign_id")
        val campaignId: Int? = null,

        @field:SerializedName("email")
        val email: String? = null,

        @field:SerializedName("lat")
        val lat: String? = null,

        @field:SerializedName("address")
        val address: Any? = null,

        @field:SerializedName("device_id")
        val deviceId: String? = null,

        @field:SerializedName("ip")
        val ip: String? = null,

        @field:SerializedName("gift_id")
        val giftId: Any? = null,

        @field:SerializedName("target_type")
        val targetType: String? = null,

        @field:SerializedName("original_target")
        val originalTarget: Any? = null,

        @field:SerializedName("is_received")
        val isReceived: Boolean? = null,

        @field:SerializedName("avatar")
        val avatar: Any? = null,

        @field:SerializedName("deleted_at")
        val deletedAt: Any? = null,

        @field:SerializedName("package_code_id")
        val packageCodeId: Any? = null,

        @field:SerializedName("winner_gifts")
        val winnerGifts: List<WinnerGiftsItem?>? = null,

        @field:SerializedName("target")
        val target: Any? = null,

        @field:SerializedName("phone")
        val phone: String? = null,

        @field:SerializedName("icheck_id")
        val icheckId: Int? = null,

        @field:SerializedName("name")
        val name: String? = null,

        @field:SerializedName("business_owner_name")
        val businessOwnerName: String? = null,

        @field:SerializedName("district_id")
        val districtId: Any? = null,

        @field:SerializedName("box_gift_id")
        val boxGiftId: Any? = null,

        @field:SerializedName("win_at")
        val winAt: String? = null,

        @field:SerializedName("ward_id")
        val wardId: Any? = null,

        @field:SerializedName("status")
        val status: String? = null,

        @field:SerializedName("city_id")
        val cityId: Any? = null
)

data class Gift(

        @field:SerializedName("image")
        val image: Image? = null,

        @field:SerializedName("owner_id")
        val ownerId: Int? = null,

        @field:SerializedName("description")
        val description: Any? = null,

        @field:SerializedName("active")
        val active: Boolean? = null,

        @field:SerializedName("created_at")
        val createdAt: String? = null,

        @field:SerializedName("campaign_count")
        val campaignCount: Any? = null,

        @field:SerializedName("type")
        val type: String? = null,

        @field:SerializedName("icoin")
        val icoin: Int? = null,

        @field:SerializedName("deleted_at")
        val deletedAt: Any? = null,

        @field:SerializedName("sponsor_type")
        val sponsorType: Any? = null,

        @field:SerializedName("updated_at")
        val updatedAt: String? = null,

        @field:SerializedName("name")
        val name: String? = null,

        @field:SerializedName("creator_id")
        val creatorId: Int? = null,

        @field:SerializedName("id")
        val id: Int? = null
)

data class WinnerGiftsItem(

        @field:SerializedName("gift")
        val gift: Gift? = null,

        @field:SerializedName("updated_at")
        val updatedAt: String? = null,

        @field:SerializedName("gift_id")
        val giftId: Int? = null,

        @field:SerializedName("winner_id")
        val winnerId: Int? = null,

        @field:SerializedName("created_at")
        val createdAt: String? = null,

        @field:SerializedName("id")
        val id: Int? = null,

        @field:SerializedName("deleted_at")
        val deletedAt: Any? = null
)

data class ListWinnerData(

        @field:SerializedName("count")
        val count: Int? = null,

        @field:SerializedName("rows")
        val rows: List<ListWinnerRowsItem?>? = null
)