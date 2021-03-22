package vn.icheck.android.loyalty.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class LuckyWheelInfoRep(

        @field:SerializedName("data")
        val data: InfoData? = null,

        @field:SerializedName("statusCode")
        val statusCode: Int? = null,

        @field:SerializedName("status")
        val status: String? = null
) : Serializable

data class InfoBox(

        @field:SerializedName("updated_at")
        val updatedAt: String? = null,

        @field:SerializedName("owner_id")
        val ownerId: Long? = null,

        @field:SerializedName("creator_id")
        val creatorId: Any? = null,

        @field:SerializedName("created_at")
        val createdAt: String? = null,

        @field:SerializedName("id")
        val id: Long? = null,

        @field:SerializedName("box_games")
        val boxGames: List<BoxGamesItem?>? = null,

        @field:SerializedName("type")
        val type: String? = null,

        @field:SerializedName("deleted_at")
        val deletedAt: Any? = null,

        @field:SerializedName("campaign_id")
        val campaignId: Long? = null
) : Serializable

data class InfoData(

        @field:SerializedName("play")
        val play: Long? = null,

        @field:SerializedName("campaign")
        val campaign: ListGameCampaign? = null
) : Serializable

data class InfoGift(

        @field:SerializedName("image")
        val image: Image? = null,

        @field:SerializedName("owner_id")
        val ownerId: Long? = null,

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
        val icoin: Long? = null,

        @field:SerializedName("deleted_at")
        val deletedAt: Any? = null,

        @field:SerializedName("sponsor_type")
        val sponsorType: Any? = null,

        @field:SerializedName("updated_at")
        val updatedAt: String? = null,

        @field:SerializedName("name")
        val name: String? = null,

        @field:SerializedName("creator_id")
        val creatorId: Long? = null,

        @field:SerializedName("id")
        val id: Long? = null
) : Serializable

data class BoxGamesItem(

        @field:SerializedName("box_id")
        val boxId: Long? = null,

        @field:SerializedName("gift")
        val gift: InfoGift? = null,

        @field:SerializedName("reason")
        val reason: Any? = null,

        @field:SerializedName("chance")
        val chance: Long? = null,

        @field:SerializedName("gift_id")
        val giftId: Long? = null,

        @field:SerializedName("is_allow_sponsor_gift")
        val isAllowSponsorGift: Boolean? = null,

        @field:SerializedName("created_at")
        val createdAt: String? = null,

        @field:SerializedName("sponsor_id")
        val sponsorId: Any? = null,

        @field:SerializedName("campaign_sponsor_id")
        val campaignSponsorId: Any? = null,

        @field:SerializedName("deleted_at")
        val deletedAt: Any? = null,

        @field:SerializedName("gift_level")
        val giftLevel: Any? = null,

        @field:SerializedName("updated_at")
        val updatedAt: String? = null,

        @field:SerializedName("quota")
        val quota: Long? = null,

        @field:SerializedName("id")
        val id: Long? = null
) : Serializable
