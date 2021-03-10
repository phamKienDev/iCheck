package vn.icheck.android.network.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ICAdsOwner(
        @SerializedName("id") val id: Long,
        @SerializedName("avatar") val avatar: ICMedia,
        @SerializedName("name") val name: String,
        @SerializedName("title") val title: String?,
        @SerializedName("verified") val verified: Boolean
) : Serializable