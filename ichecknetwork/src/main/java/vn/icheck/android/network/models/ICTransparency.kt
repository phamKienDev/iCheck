package vn.icheck.android.network.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ICTransparency(
        @Expose val id: Long?,
        @Expose var total: Int?,
        @Expose var isVoted: Boolean?,
        @Expose var totalVoted: Int?,
        @Expose var totalNoVoted: Int?
)