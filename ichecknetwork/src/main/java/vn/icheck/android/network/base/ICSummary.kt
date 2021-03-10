package vn.icheck.android.network.base

import com.google.gson.annotations.SerializedName

data class ICSummary(
        @SerializedName("number_gift") val numberGift: Int,
        @SerializedName("rank_level_name") val rankLevelName: String,
        @SerializedName("next_time_reset") val nextTimeReset: String,
        @SerializedName("level_point_require") val levelPointRequire: Int,
        @SerializedName("available_point") val availablePoint: Long,
        @SerializedName("current_point") val currentPoint: Int,
        @SerializedName("rank_level") val rankLevel: Int,
        @SerializedName("rank_benefit") val rankBenefit: MutableList<ICBenefit>,
        @SerializedName("walletId") val walletId: String,
        @SerializedName("balance") val balance: Long,
        @SerializedName("availableBalance") val availableBalance: Long
)