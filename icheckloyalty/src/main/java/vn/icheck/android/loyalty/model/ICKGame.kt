package vn.icheck.android.loyalty.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ICKGame(

        @field:SerializedName("status_title")
        val statusTitle: String? = null,

        @field:SerializedName("publish_type")
        val publishType: Any? = null,

        @field:SerializedName("reason")
        val reason: Any? = null,

        @field:SerializedName("end_at")
        val endAt: String? = null,

        @field:SerializedName("game")
        val game: Game? = null,

        @field:SerializedName("has_chance_code")
        val hasChanceCode: Boolean? = null,

        @field:SerializedName("status_time_title")
        val statusTimeTitle: String? = null,

        @field:SerializedName("owner_id")
        val ownerId: Long? = null,

        @field:SerializedName("theme_id")
        val themeId: Long? = null,

        @field:SerializedName("background_rotation")
        val backgroundRotation: ICThumbnail? = null,

        @field:SerializedName("description")
        val description: String? = null,

        @field:SerializedName("created_at")
        val createdAt: String? = null,

        @field:SerializedName("is_require_icheck_app")
        val isRequireIcheckApp: Boolean? = null,

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

        @field:SerializedName("id")
        val id: Long? = null,

        @field:SerializedName("business_owner_id")
        val businessOwnerId: Long? = null,

        @field:SerializedName("status_time")
        val statusTime: String? = null,

        @field:SerializedName("campaign_game_user")
        val campaignGameUser: List<CampaignGameUserItem?>? = null,

        @field:SerializedName("statistic_winner_accumulate_point")
        val statisticWinnerAccumulatePoint: List<ICKPointUser>? = null,

        @field:SerializedName("game_id")
        val gameId: Long? = null,

        @field:SerializedName("image")
        val image: ICThumbnail? = null,

        @field:SerializedName("title_button")
        val titleButton: String? = null,

        @field:SerializedName("theme_image")
        val themeImage: Any? = null,

        @field:SerializedName("schema_game_id")
        val schemaGameId: Any? = null,

        @field:SerializedName("target_type")
        val targetType: String? = null,

        @field:SerializedName("schema_button")
        val schemaButton: String? = null,

        @field:SerializedName("export_gift_to")
        val exportGiftTo: String? = null,

        @field:SerializedName("landing_page")
        val landingPage: String? = null,

        @field:SerializedName("deleted_at")
        val deletedAt: Any? = null,

        @field:SerializedName("benefit")
        val benefit: Long? = null,

        @field:SerializedName("header_image_rotation")
        val headerImageRotation: ICThumbnail? = null,

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
        val owner: ICKOwner? = null,

        @field:SerializedName("box")
        val box: ICKBox? = null
) : Serializable

data class CampaignGameUserItem(

        @field:SerializedName("play")
        val play: Long? = null,

        @field:SerializedName("updated_at")
        val updatedAt: String? = null,

        @field:SerializedName("icheck_id")
        val icheckId: Long? = null,

        @field:SerializedName("times_play")
        val timesPlay: Long? = null,

        @field:SerializedName("is_synced_anonymous_icheck")
        val isSyncedAnonymousIcheck: Any? = null,

        @field:SerializedName("created_at")
        val createdAt: String? = null,

        @field:SerializedName("campaign")
        val campaign: ListGameCampaign? = null,

        @field:SerializedName("id")
        val id: Long? = null,

        @field:SerializedName("deleted_at")
        val deletedAt: Any? = null,

        @field:SerializedName("campaign_id")
        val campaignId: Long? = null
) : Serializable

data class Game(

        @field:SerializedName("image")
        val image: String? = null,

        @field:SerializedName("code")
        val code: String? = null,

        @field:SerializedName("updated_at")
        val updatedAt: Any? = null,

        @field:SerializedName("name")
        val name: String? = null,

        @field:SerializedName("is_public")
        val isPublic: Boolean? = null,

        @field:SerializedName("created_at")
        val createdAt: Any? = null,

        @field:SerializedName("id")
        val id: Long? = null,

        @field:SerializedName("deleted_at")
        val deletedAt: Any? = null
) : Serializable
