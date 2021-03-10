package vn.icheck.android.network.models.detail_stamp_v6


import com.google.gson.annotations.Expose
import java.io.Serializable

data class ICObjectMessageV6(
        @Expose
        var message: String? = null,
        @Expose
        var type: String? = null,
        @Expose
        var service: ICObjectServiceV6? = null,
        @Expose
        var priority: Int? = null
): Serializable