package vn.icheck.android.component.sponsor_feed

import com.google.gson.annotations.Expose
import vn.icheck.android.network.models.post.ICProductInPost
import java.io.Serializable

data class ICSponsorFeed(
        @Expose
        val id: Long,
        @Expose
        var logoEnterprise: String?,
        @Expose
        var name: String?,
        @Expose
        var verified: Boolean,
        @Expose
        var description: String?,
        @Expose
        var product: ICProductInPost?
)

