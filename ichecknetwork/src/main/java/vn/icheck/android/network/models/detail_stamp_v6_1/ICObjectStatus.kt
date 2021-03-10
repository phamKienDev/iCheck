package vn.icheck.android.network.models.detail_stamp_v6_1

import com.google.gson.annotations.Expose
import java.io.Serializable

data class ICObjectStatus(
        @Expose
        var id: Long? = null,
        @Expose
        var name: String? = null,
        @Expose
        var step: Int? = null,
        @Expose
        var pre_status: Int? = null,
        @Expose
        var description: String? = null,
        @Expose
        var inst_dt: String? = null
): Serializable