package vn.icheck.android.network.models

import com.google.gson.annotations.SerializedName

data class ICLayout(
        @SerializedName("id") val id: String? = "",
        @SerializedName("key") var key: String = "",
        @SerializedName("request") val request: ICRequest = ICRequest(),
        @SerializedName("custom") val custom: ICCustom? = null,
        @SerializedName("entityIdList") val entityIdList: MutableList<Long>? = null,
        var viewType: Int = 0,
        var data: Any? = null,
        var subType: Int = 0
)