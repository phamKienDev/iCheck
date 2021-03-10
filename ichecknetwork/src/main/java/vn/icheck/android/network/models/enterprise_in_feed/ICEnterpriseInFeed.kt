package vn.icheck.android.network.models.enterprise_in_feed

import com.google.gson.annotations.Expose
import java.io.Serializable

data class ICEnterpriseInFeed(
        @Expose
        var id: Long?,
        @Expose
        var enterprise_id: Long?,
        @Expose
        var image: String?,
        @Expose
        var name: String?,
        @Expose
        var isVerify: Boolean?
) : Serializable