package vn.icheck.android.network.models.detail_stamp_v6_1

import com.google.gson.annotations.Expose
import java.io.Serializable

data class ICObjectCount(
        @Expose
        var prefix: String? = null,
        @Expose
        var number: Long? = null,
        @Expose
        var user_id: Long? = null,
        @Expose
        var icheck_id: String? = null,
        @Expose
        var device_id: String? = null,
        @Expose
        var active_count: Int? = null,
        @Expose
        var scan_count: Int? = null,
        @Expose
        var people_count: Int? = null
): Serializable