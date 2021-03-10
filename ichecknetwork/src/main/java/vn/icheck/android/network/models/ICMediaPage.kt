package vn.icheck.android.network.models

import com.google.gson.annotations.Expose
import java.io.Serializable

data class ICMediaPage (
    @Expose
    var id: Long?,
    @Expose
    var userId: Long?,
    @Expose
    var pageId: Long?,
    @Expose
    var postId: Long?,
    @Expose
    var content: String?,
    @Expose
    var type: String?,
    @Expose
    var createdAt: String?,
    @Expose
    var updatedAt: String?) : Serializable