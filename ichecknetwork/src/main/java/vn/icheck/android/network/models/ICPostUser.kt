package vn.icheck.android.network.models

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class ICPostUser(
        @SerializedName("id") val id: Long,
        @SerializedName("icheckId") val icheckId: Long?,
        @SerializedName("firstName") val firstName: String?,
        @SerializedName("lastName") val lastName: String?,
        @SerializedName("name") val name: String?,
        @SerializedName("avatar") val avatar: String?,
        @SerializedName("rank") val rank: Int
) : Serializable