package vn.icheck.android.network.base

import com.google.gson.annotations.SerializedName

data class ICBenefit(
        @SerializedName("rank_point") val rankPoint: Int,
        @SerializedName("rank_level_name") val rankLevelName: String,
        @SerializedName("rank_level") val rankLevel: Int
)