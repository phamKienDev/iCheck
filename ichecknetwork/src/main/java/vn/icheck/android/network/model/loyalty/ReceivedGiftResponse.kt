package vn.icheck.android.network.model.loyalty

import com.google.gson.annotations.SerializedName

data class ReceivedGiftResponse(

        @field:SerializedName("data")
        val data: ReceivedGiftData? = null,

        @field:SerializedName("statusCode")
        val statusCode: String? = null
)

data class ReceivedGiftItem(

        @field:SerializedName("image")
        val image: String? = null,

        @field:SerializedName("number")
        val number: Int? = null,

        @field:SerializedName("expiredAt")
        val expiredAt: String? = null,

        @field:SerializedName("receiveAt")
        val receiveAt: String? = null,

        @field:SerializedName("rewardType")
        val rewardType: String? = null,

        @field:SerializedName("businessName")
        val businessName: String? = null,

        @field:SerializedName("remainTime")
        val remainTime: String? = null,

        @field:SerializedName("id")
        val id: String? = null,

        @field:SerializedName("state")
        val state: Int? = null,

        @field:SerializedName("type")
        val type: Int? = null,

        @field:SerializedName("title")
        val title: String? = null,

        @field:SerializedName("businessType")
        val businessType: Int? = null,

        @field:SerializedName("logo")
        val logo: String? = null,
        @field:SerializedName("name")
        val name: String? = null,
        @field:SerializedName("value")
        val value:Long? = null
)

data class ReceivedGiftData(

        @field:SerializedName("count")
        val count: Int? = null,

        @field:SerializedName("rows")
        val rows: List<ReceivedGiftItem>? = null
)
