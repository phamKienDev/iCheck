package vn.icheck.android.network.models

data class ICProductContribution(
        val contribution: Contribution? = null,
        var isMe: Boolean = false,
        var count: Int? = null,
        val userContributions: MutableList<String>? = null
) {
    data class Contribution(
            val id: Long? = null,
            val user: ICUser? = null,
            var upVotes: Int = 0,
            var downVotes: Int = 0,
            var myVote: Boolean? = null
    )
}
