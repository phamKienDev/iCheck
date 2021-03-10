package vn.icheck.android.network.models.detail_stamp_v6_1

import com.google.gson.annotations.Expose
import java.io.Serializable

data class ICObjectChildField(
        @Expose
        var value: String? = null,
        @Expose
        var isText: Boolean? = null
): Serializable