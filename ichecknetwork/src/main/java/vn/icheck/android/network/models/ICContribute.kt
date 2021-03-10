package vn.icheck.android.network.models

import com.google.gson.annotations.Expose

class ICContribute {
    @Expose
    var id: Long = 0

    @Expose
    var upVotes: Int = 0

    @Expose
    var downVotes: Int = 0

    @Expose
    var productId: Long = 0

    @Expose
    var isMe: Boolean = false

    @Expose
    var isVote: Boolean = false

    @Expose
    var contributionVote: ICContributionVote? = null

    @Expose
    var data: ICProduct? = null

    @Expose
    var user: ICUser? = null

    @Expose
    var myVote: Boolean? = null
}