package vn.icheck.android.network.models.history

import com.google.gson.annotations.Expose
import java.io.Serializable

data class ICBigCorp(
        @Expose var id: Long? = null,
        @Expose var name: String? = null,
        @Expose var avatar: String? = null,
        @Expose var cover: String? = null,
        var avatar_all: Int? = null
) : Serializable