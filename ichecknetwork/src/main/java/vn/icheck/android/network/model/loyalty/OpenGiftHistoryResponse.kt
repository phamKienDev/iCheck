package vn.icheck.android.network.model.loyalty

import com.google.gson.annotations.SerializedName

data class OpenGiftHistoryResponse(

        @field:SerializedName("data")
        val data: OpenGiftHistoryData? = null,

        @field:SerializedName("statusCode")
        val statusCode: String? = null
)

data class OpenGiftHistoryItem(

        @field:SerializedName("iconId")
        val iconId: Int? = null,

        @field:SerializedName("id")
        val id: String? = null,

        @field:SerializedName("iconUrl")
        val iconUrl: String? = null,

        @field:SerializedName("title")
        val title: String? = null,

        @field:SerializedName("time")
        val time: String? = null
)

data class OpenGiftHistoryData(

        @field:SerializedName("count")
        val count: Int? = null,

        @field:SerializedName("rows")
        val rows: List<OpenGiftHistoryItem?>? = null
)
