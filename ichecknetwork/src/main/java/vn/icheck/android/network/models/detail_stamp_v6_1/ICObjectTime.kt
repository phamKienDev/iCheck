package vn.icheck.android.network.models.detail_stamp_v6_1

import com.google.gson.annotations.Expose
import java.io.Serializable

data class ICObjectTime(
        @Expose
        var active: String? = null,
        @Expose
        var guarantee_days: Long? = null,
        @Expose
        var days_remaining: Long? = null,
        @Expose
        var days_remaining_str: String? = null,
        @Expose
        var expired_date: String? = null,
        @Expose
        var guarantee_days_update: Int? = null,
        @Expose
        var type_guarantee_day: String? = null
) : Serializable