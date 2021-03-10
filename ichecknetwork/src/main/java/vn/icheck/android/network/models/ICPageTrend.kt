package vn.icheck.android.network.models

import com.google.gson.annotations.Expose

class ICPageTrend {
    @Expose
    val id: Long = 0

    @Expose
    val name: String? = null

    @Expose
    val avatar: String? = null

    @Expose
    val cover: String? = null

    @Expose
    val isVerify: Boolean? = null

    @Expose
    val follower: Int? = null

    @Expose
    val followCount: Int = 0

    @Expose
    val objectType: String? = null

    @Expose
    val likeCount: Long = 0

    @Expose
    val followers: MutableList<ICFollowers>? = null

    var isFollow = false
}

class ICFollowers{
    @Expose
    val name: String? = null

    @Expose
    val avatar: String? = null
}