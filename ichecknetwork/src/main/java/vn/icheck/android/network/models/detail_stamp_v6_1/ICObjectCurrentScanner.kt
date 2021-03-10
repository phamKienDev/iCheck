package vn.icheck.android.network.models.detail_stamp_v6_1

import com.google.gson.annotations.Expose
import java.io.Serializable

data class ICObjectCurrentScanner(

        @Expose
        var customer_id: Long? = null,
        @Expose
        var active_time: String? = null
): Serializable