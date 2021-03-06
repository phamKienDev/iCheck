package vn.icheck.android.network.model

import com.google.gson.JsonElement
import com.google.gson.annotations.SerializedName
import vn.icheck.android.ichecklibs.util.getString
import vn.icheck.android.ichecklibs.util.setText
import vn.icheck.android.network.R

data class IckLayoutResponse(

    @field:SerializedName("layout")
        val layout: List<LayoutItem?>? = null,

    @field:SerializedName("statusCode")
        val statusCode: String? = null,

    @field:SerializedName("message") val message: String? = getString(R.string.khong_tai_duoc_du_lieu_vui_long_thu_lai),

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
