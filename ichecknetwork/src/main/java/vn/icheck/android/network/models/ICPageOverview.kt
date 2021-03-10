package vn.icheck.android.network.models

import com.google.gson.annotations.Expose
import java.io.Serializable

class ICPageOverview : Serializable {
    @Expose
    var id: Long? = null

    @Expose
    var name: String? = null

    @Expose
    var description: String? = null

    @Expose
    var productCount: Long? = null

    @Expose
    var scanCount: Long? = null

    @Expose
    var followers: MutableList<ICUserFollowPage>? = null

    @Expose
    val isVerify: Boolean = false

    @Expose
    var avatar: String? = null

    @Expose
    var rating: Double = 0.0

    @Expose
    var followCount: Long = 0

    @Expose
    var likeCount: Long? = null

    @Expose
    var likedCountOnPosts: Long? = null

    var isFollow: Boolean = false

    var unsubscribeNotice: Boolean = false

    var buttonConfigs: ArrayList<ICButtonOfPage>? = null

    var pageDetail: ICPageDetail? = null

    var isIgnoreInvite: Boolean = true
}