package vn.icheck.android.network.models

import com.google.gson.annotations.SerializedName

data class ICPopup(
    @SerializedName("clickCount")
    val clickCount: Int?=null,
    @SerializedName("createdAt")
    val createdAt: String,
    @SerializedName("deeplink")
    val deeplink: String?=null,
    @SerializedName("deeplinkParams")
    val deeplinkParams: String?=null,
    @SerializedName("deletedAt")
    val deletedAt: String?=null,
    @SerializedName("describe")
    val describe: Any?,
    @SerializedName("displayType")
    val displayType: String,
    @SerializedName("document")
    val document: String?=null,
    @SerializedName("endTime")
    val endTime: String?=null,
    @SerializedName("id")
    val id: Int,
    @SerializedName("image")
    val image: String?=null,
    @SerializedName("name")
    val name: String,
    @SerializedName("path")
    val path: String?=null,
    @SerializedName("startTime")
    val startTime: String,
    @SerializedName("status")
    val status: String,
    @SerializedName("updatedAt")
    val updatedAt: String,
    @SerializedName("url")
    val url: String?=null,
    @SerializedName("viewCount")
    val viewCount: Int?=null
)