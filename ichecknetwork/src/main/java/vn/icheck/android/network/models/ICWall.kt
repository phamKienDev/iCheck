package vn.icheck.android.network.models

import com.google.gson.annotations.SerializedName

data class ICWall(
        @SerializedName("id") val id: Long,
        @SerializedName("corver") val corver: String,
        @SerializedName("verifyStatus") val verifyStatus: Int,
        @SerializedName("followingUserCount") val followingUserCount: Int,
        @SerializedName("followedUserCount") val followedUserCount: Int,
        @SerializedName("followingPageCount") val followingPageCount: Int
)