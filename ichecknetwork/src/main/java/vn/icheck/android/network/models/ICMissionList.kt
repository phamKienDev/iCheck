package vn.icheck.android.network.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ICMissionList(
      @Expose
      @SerializedName("count")
      val count: Int,
      @Expose
      @SerializedName("rows")
      val missions: List<MissionChild>
)

data class MissionChild(
        @Expose
        @SerializedName("mission_name")
        val missionName: String,
        @Expose
        @SerializedName("campaign_name")
        val campaignName: String,
        @Expose
        @SerializedName("image")
        val image: String,
        @Expose
        @SerializedName("end_at")
        val endAt: String,
        @Expose
        @SerializedName("logo")
        val logo: String,
        @Expose
        @SerializedName("current_event")
        var currentEvent: Int,
        @Expose
        @SerializedName("success_number")
        var successNumber: Int,
        @Expose
        @SerializedName("id")
        val id: String,
        @Expose
        @SerializedName("state")
        var state: Int,
        @Expose
        @SerializedName("total_event")
        var totalEvent: Int,
        @Expose
        @SerializedName("finish_state")
        var finishState: Int,
        @SerializedName("finished_at")
        var finishedAt: String,
        @Expose
        @SerializedName("begin_at")
        val beginAt: String)