package vn.icheck.android.network.models

import com.google.gson.annotations.Expose
import java.io.Serializable

data class ICDetailGiftStore(
        @Expose
        var id: Long? = null,
        @Expose
        var image: String? = null,
        @Expose
        var name: String? = null,
        @Expose
        var remain: Long? = null,
        @Expose
        var desc: String? = null,
        @Expose
        var point_exchange: Long? = null
) : Serializable