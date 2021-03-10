package vn.icheck.android.network.models.detail_stamp_v6_1

import com.google.gson.annotations.Expose
import java.io.Serializable

data class ICMessageErrorStamp(
        @Expose
        var code: Int? = null,
        @Expose
        var message: String? = null
) : Serializable