package vn.icheck.android.network.models

import com.google.gson.annotations.Expose

class ICReqUpdatePost {
    @Expose var content: String? = null
    @Expose var media: List<ICMedia>? = null
    @Expose var targetType: String? = null
    @Expose var targetId: Long? = null
    @Expose var privacyElementId: Long = 0
}