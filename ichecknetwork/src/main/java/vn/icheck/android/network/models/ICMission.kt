package vn.icheck.android.network.models

import com.google.gson.annotations.Expose

data class ICMission(
        @Expose val id: String = "",
        @Expose val missionName: String = "",
        @Expose val name: String = "",
        @Expose val campaignName: String = "",
        @Expose val logo: String = "",
        @Expose val totalEvent: Int = 0,
        @Expose var currentEvent: Int = 0,
        @Expose val beginAt: String = "",
        @Expose var endAt: String = "",
        @Expose val successNumber: Int = 0,
        @Expose var state: Int = 0, // 1 - Đang diễn ra, 2 - Đã kết thúc, còn lại là chưa diễn ra
        @Expose var finishState: Int = 0,//1-Đang tham gia, 2-Đã hoàn thành, 3-Không hoàn thành
        @Expose val image: String? = null,
        @Expose val event: String = "",
        @Expose val eventName: String = "",
        @Expose val boxAmount: Int = 0
)