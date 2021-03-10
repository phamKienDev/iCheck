package vn.icheck.android.loyalty.model

import com.google.gson.annotations.SerializedName

data class LuckyWheelInfoRep(

        @field:SerializedName("data")
        val data: InfoData? = null,

        @field:SerializedName("statusCode")
        val statusCode: Int? = null,

        @field:SerializedName("status")
        val status: String? = null
)

data class InfoCampaign(

        @field:SerializedName("status_title")
        val statusTitle: String? = null,

        @field:SerializedName("publish_type")
        val publishType: Any? = null,

        @field:SerializedName("reason")
        val reason: Any? = null,

        @field:SerializedName("end_at")
        val endAt: String? = null,

        @field:SerializedName("has_chance_code")
        val hasChanceCode: Boolean? = null,

        @field:SerializedName("status_time_title")
        val statusTimeTitle: String? = null,

        @field:SerializedName("owner_id")
        val ownerId: Long? = null,

        @field:SerializedName("theme_id")
        val themeId: Long? = null,

        @field:SerializedName("description")
        val description: String? = null,

        @field:SerializedName("created_at")
        val createdAt: String? = null,

        @field:SerializedName("box")
        val box: InfoBox? = null,

        @field:SerializedName("type")
        val type: String? = null,

        @field:SerializedName("start_at")
        val startAt: String? = null,

        @field:SerializedName("publish_at")
        val publishAt: String? = null,

        @field:SerializedName("balance")
        val balance: Long? = null,

        @field:SerializedName("updated_at")
        val updatedAt: String? = null,

        @field:SerializedName("theme")
        val theme: Theme? = null,

        @field:SerializedName("id")
        val id: Long? = null,

        @field:SerializedName("business_owner_id")
        val businessOwnerId: Long? = null,

        @field:SerializedName("status_time")
        val statusTime: String? = null,

        @field:SerializedName("game_id")
        val gameId: Long? = null,

        @field:SerializedName("image")
        val image: Image? = null,

        @field:SerializedName("theme_image")
        val themeImage: Any? = null,

        @field:SerializedName("target_type")
        val targetType: String? = null,

        @field:SerializedName("export_gift_to")
        val exportGiftTo: String? = null,

        @field:SerializedName("deleted_at")
        val deletedAt: Any? = null,

        @field:SerializedName("benefit")
        val benefit: Long? = null,

        @field:SerializedName("user_count")
        val userCount: Any? = null,

        @field:SerializedName("export_gift_from")
        val exportGiftFrom: String? = null,

        @field:SerializedName("winner_count")
        val winnerCount: Long? = null,

        @field:SerializedName("name")
        val name: String? = null,

        @field:SerializedName("creator_id")
        val creatorId: Long? = null,

        @field:SerializedName("status")
        val status: String? = null,

        @field:SerializedName("owner")
        val owner: Owner? = null
)

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
)

data class ImageGameLuckywheelSix(

        @field:SerializedName("small")
        val small: String? = null,

        @field:SerializedName("square")
        val square: String? = null,

        @field:SerializedName("thumbnail")
        val thumbnail: String? = null,

        @field:SerializedName("original")
        val original: String? = null,

        @field:SerializedName("id")
        val id: String? = null,

        @field:SerializedName("medium")
        val medium: String? = null
)

data class ImageGameLuckywheelButton(

        @field:SerializedName("small")
        val small: String? = null,

        @field:SerializedName("square")
        val square: String? = null,

        @field:SerializedName("thumbnail")
        val thumbnail: String? = null,

        @field:SerializedName("original")
        val original: String? = null,

        @field:SerializedName("id")
        val id: String? = null,

        @field:SerializedName("medium")
        val medium: String? = null
)

data class Theme(

        @field:SerializedName("created_at")
        val createdAt: String? = null,

        @field:SerializedName("avatar")
        val avatar: Avatar? = null,

        @field:SerializedName("deleted_at")
        val deletedAt: Any? = null,

        @field:SerializedName("cover")
        val cover: Cover? = null,

        @field:SerializedName("updated_at")
        val updatedAt: String? = null,

        @field:SerializedName("name")
        val name: String? = null,

        @field:SerializedName("is_public")
        val isPublic: Boolean? = null,

        @field:SerializedName("image_game_luckywheel_six")
        val imageGameLuckywheelSix: ImageGameLuckywheelSix? = null,

        @field:SerializedName("id")
        val id: Long? = null,

        @field:SerializedName("image_game_luckywheel_eight")
        val imageGameLuckywheelEight: ImageGameLuckywheelEight? = null,

        @field:SerializedName("image_game_luckywheel_ten")
        val imageGameLuckywheelTen: ImageGameLuckywheelTen? = null,

        @field:SerializedName("image_game_luckywheel_button")
        val imageGameLuckywheelButton: ImageGameLuckywheelButton? = null,

        @field:SerializedName("game_id")
        val gameId: Long? = null
)

data class ImageGameLuckywheelTen(

        @field:SerializedName("small")
        val small: String? = null,

        @field:SerializedName("square")
        val square: String? = null,

        @field:SerializedName("thumbnail")
        val thumbnail: String? = null,

        @field:SerializedName("original")
        val original: String? = null,

        @field:SerializedName("id")
        val id: String? = null,

        @field:SerializedName("medium")
        val medium: String? = null
)

data class InfoData(

        @field:SerializedName("play")
        val play: Long? = null,

        @field:SerializedName("campaign")
        val campaign: InfoCampaign? = null
)

//data class Image(
//
//	@field:SerializedName("small")
//	val small: String? = null,
//
//	@field:SerializedName("square")
//	val square: String? = null,
//
//	@field:SerializedName("thumbnail")
//	val thumbnail: String? = null,
//
//	@field:SerializedName("original")
//	val original: String? = null,
//
//	@field:SerializedName("id")
//	val id: String? = null,
//
//	@field:SerializedName("medium")
//	val medium: String? = null
//)

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
)

data class ImageGameLuckywheelEight(

        @field:SerializedName("small")
        val small: String? = null,

        @field:SerializedName("square")
        val square: String? = null,

        @field:SerializedName("thumbnail")
        val thumbnail: String? = null,

        @field:SerializedName("original")
        val original: String? = null,

        @field:SerializedName("id")
        val id: String? = null,

        @field:SerializedName("medium")
        val medium: String? = null
)

data class Avatar(

        @field:SerializedName("small")
        val small: String? = null,

        @field:SerializedName("square")
        val square: String? = null,

        @field:SerializedName("thumbnail")
        val thumbnail: String? = null,

        @field:SerializedName("original")
        val original: String? = null,

        @field:SerializedName("id")
        val id: String? = null,

        @field:SerializedName("medium")
        val medium: String? = null
)

data class Cover(

        @field:SerializedName("small")
        val small: String? = null,

        @field:SerializedName("square")
        val square: String? = null,

        @field:SerializedName("thumbnail")
        val thumbnail: String? = null,

        @field:SerializedName("original")
        val original: String? = null,

        @field:SerializedName("id")
        val id: String? = null,

        @field:SerializedName("medium")
        val medium: String? = null
)

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
)
