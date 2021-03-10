package vn.icheck.android.network.models

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ICContributionVote(
        @Expose
        @SerializedName("upvotes")
        var voted: Int,
        @Expose
        @SerializedName("downvotes")
        var noVoted: Int,
        @Expose
        @SerializedName("myVote")
        var myVote: Boolean? = null
)