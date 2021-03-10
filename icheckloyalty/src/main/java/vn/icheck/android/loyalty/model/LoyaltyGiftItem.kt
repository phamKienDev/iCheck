package vn.icheck.android.loyalty.model

import com.google.gson.annotations.SerializedName

data class LoyaltyGiftItem(

        @field:SerializedName("gift")
        val gift: Gift? = null,

        @field:SerializedName("business_loyalty_id")
        val businessLoyaltyId: Int? = null,

        @field:SerializedName("quantity")
        val quantity: Int? = null,

        @field:SerializedName("owner_id")
        val ownerId: Int? = null,

        @field:SerializedName("gift_id")
        val giftId: Int? = null,

        @field:SerializedName("created_at")
        val createdAt: String? = null,

        @field:SerializedName("quantity_exchanged")
        val quantityExchanged: Int? = null,

        @field:SerializedName("point_name")
        val pointName: String? = null,

        @field:SerializedName("point_exchange")
        val pointExchange: Long? = null,

        @field:SerializedName("deleted_at")
        val deletedAt: Any? = null,

        @field:SerializedName("banned_reason")
        val bannedReason: String? = null,

        @field:SerializedName("updated_at")
        val updatedAt: String? = null,

        @field:SerializedName("creator_id")
        val creatorId: Long? = null,

        @field:SerializedName("id")
        val id: Long? = null,

        @field:SerializedName("state")
        val state: Int? = null,

        @field:SerializedName("quantity_remain")
        val quantityRemain: Long? = null,

        @field:SerializedName("total_icoin")
        val totalIcoin: Int? = null,

        @field:SerializedName("status")
        val status: Int? = null,

        @field:SerializedName("used_icoin")
        val usedIcoin: Int? = null
)