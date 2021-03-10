package vn.icheck.android.network.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ICMissionDetail(
        @SerializedName("id") val id: String,
        @SerializedName("campaignId") val campaignId: String?,
        @SerializedName("missionName") val missionName: String,
        @SerializedName("beginAt") val beginAt: String?,
        @SerializedName("endAt") val endAt: String?,
        @SerializedName("finishedAt") val finishedAt: String?,
        @SerializedName("totalBox") val totalBox: Int,
        @SerializedName("description") val description: String?,
        @SerializedName("guide") val guide: String?,
        @SerializedName("campaignImage") val campaignImage: String?,
        @SerializedName("totalEvent") val totalEvent: Int,
        @SerializedName("currentEvent") val currentEvent: Int,
        @SerializedName("finishState") val finishState: Int, // 1-Đang tham gia, 2-Đã hoàn thành, 3-Không hoàn thành
        @SerializedName("rewardBoxes") val rewardBoxes: MutableList<ICBoxReward>?,
        @SerializedName("products") val products: MutableList<ICProduct>?,
        @SerializedName("shops") val shops: MutableList<ICShop>?,
        @SerializedName("categories") val categories: MutableList<ICCategory>?,
        @SerializedName("companies") val companies: MutableList<ICCompany>?,
        @SerializedName("surveys") val surveys: MutableList<ICSurvey>?,
        @SerializedName("logo") val logo: String?,
        @SerializedName("image") val image: String?,
        @SerializedName("state") var state: Int?, // 1 - Đang diễn ra, 2 - Đã kết thúc, còn lại là chưa diễn ra
        @SerializedName("event") val event: String?,
        @SerializedName("eventName") val eventName: String?,
        @SerializedName("buttonName") val buttonName: String?,
        @SerializedName("buttonTarget") val buttonTarget: String?,
        @SerializedName("buttonID") val buttonID: String?
):Serializable