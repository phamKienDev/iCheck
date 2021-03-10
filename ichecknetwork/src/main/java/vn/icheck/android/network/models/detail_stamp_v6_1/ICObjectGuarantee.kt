package vn.icheck.android.network.models.detail_stamp_v6_1

import com.google.gson.annotations.Expose
import java.io.Serializable

data class ICObjectGuarantee(
        @Expose
        var is_device_active: Boolean? = null,
        @Expose
        var customer_id: Long? = null,
        @Expose
        var time: ICObjectTime? = null,
        @Expose
        var last_guarantee: ICObjectLastGuarantee? = null
): Serializable