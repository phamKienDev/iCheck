package vn.icheck.android.network.models

import com.google.gson.annotations.SerializedName
import kotlin.Any

data class AvatarThumbnails(

        @field:SerializedName("small")
	val small: Any? = null,

        @field:SerializedName("square")
	val square: Any? = null,

        @field:SerializedName("thumbnail")
	val thumbnail: Any? = null,

        @field:SerializedName("original")
	val original: Any? = null,

        @field:SerializedName("medium")
	val medium: Any? = null
)