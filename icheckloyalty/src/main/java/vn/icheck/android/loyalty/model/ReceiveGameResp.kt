package vn.icheck.android.loyalty.model

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ReceiveGameResp(

        @field:SerializedName("data")
        val data: DataReceiveGameResp? = null,

        @field:SerializedName("statusCode")
        val statusCode: Int? = null,

        @field:SerializedName("status")
        val status: String? = null
)

data class Campaign(

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

        @Expose
        val background_rotation: ICThumbnail? = null,

        @Expose
        val header_image_rotation: ICThumbnail? = null,

        @Expose
        val landing_page: String? = null,

        @Expose
        val title_button: String? = null,

        @Expose
        val schema_button: String? = null,

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
        val status: String? = null
)

data class DataReceiveGameResp(

        @field:SerializedName("play")
        val play: Long? = null,

        @field:SerializedName("campaign")
        val campaign: Campaign? = null,

        @field:SerializedName("message")
        val message: String?
)