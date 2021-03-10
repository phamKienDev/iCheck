package vn.icheck.android.loyalty.model

import com.google.gson.annotations.Expose

data class ICKRewardGameVQMMLoyalty(
        val gift: ICKGift? = null,
        val updatedAt: String? = null,
        val customerIcheckId: Long? = null,
        val giftId: Long? = null,
        @Expose
        val winner_id: Long? = null,
        val created_at: String? = null,
        val id: Long? = null,
        val deletedAt: Any? = null,
        val campaignId: Long? = null
)