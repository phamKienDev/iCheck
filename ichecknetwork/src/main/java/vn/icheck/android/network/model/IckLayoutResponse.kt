package vn.icheck.android.network.model

import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName

data class IckLayoutResponse(

        @field:SerializedName("layout")
        val layout: List<LayoutItem?>? = null,

        @field:SerializedName("statusCode")
        val statusCode: String? = null,

        @field:SerializedName("message") val message: String? = "Không tải được dữ liệu. Vui lòng thử lại.",

        @field:SerializedName("data") val data: JsonElement?
)

data class LayoutCustom(
        val any: Any? = null
)

data class LayoutItem(

        @field:SerializedName("request")
        val request: LayoutRequest? = null,

        @field:SerializedName("custom")
        val custom: LayoutCustom? = null,

        @field:SerializedName("id")
        val id: String? = null,

        @field:SerializedName("key")
        val key: Any? = null
)

data class LayoutRequest(

        @field:SerializedName("type")
        val type: String? = null,

        @field:SerializedName("url")
        val url: String? = null
)
