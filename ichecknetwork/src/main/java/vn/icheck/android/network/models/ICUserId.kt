package vn.icheck.android.network.models

import com.google.gson.annotations.SerializedName
import kotlin.Any

data class ICUserId(

        @field:SerializedName("name")
	val name: String? = null,

        @field:SerializedName("verified")
	val verified: Boolean? = null,

        @field:SerializedName("avatar_thumbnails")
	val avatarThumbnails: AvatarThumbnails? = null,

        @field:SerializedName("id")
	val id: Long? = null,

        @field:SerializedName("avatar")
	val avatar: Any? = null,

        @field:SerializedName("type")
	val type: String? = null
)