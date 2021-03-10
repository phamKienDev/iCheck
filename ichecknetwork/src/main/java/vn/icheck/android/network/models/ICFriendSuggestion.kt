package vn.icheck.android.network.models

import com.google.gson.annotations.SerializedName

data class ICFriendSuggestion(
        @SerializedName("id") val id: Long,
        @SerializedName("icheckId") val icheckId: Long,
        @SerializedName("firstName") val firstName: String?,
        @SerializedName("lastName") val lastName: String?,
        @SerializedName("avatar") val avatar: String?,
        @SerializedName("rank") val rank: Int,
        var isSend: Boolean? = null
)