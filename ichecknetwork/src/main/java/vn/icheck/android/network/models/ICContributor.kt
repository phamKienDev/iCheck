package vn.icheck.android.network.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ICContributor (
        @Expose
        @SerializedName("id")
        var id: Int,
        @Expose
        @SerializedName("type")
        val type: String,
        @Expose
        @SerializedName("name")
        val name: String,
        @Expose
        @SerializedName("avatar")
        val avatar: String,
        @Expose
        @SerializedName("verified")
        var verified: Boolean
)