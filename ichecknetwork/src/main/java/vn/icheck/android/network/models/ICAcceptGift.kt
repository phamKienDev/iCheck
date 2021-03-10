package vn.icheck.android.network.models

import com.google.gson.annotations.Expose
import java.io.Serializable

data class ICAcceptGift(
        @Expose
        var code: Int? = null,
        @Expose
        var message: String? = null,
        @Expose
        var order_id: Long? = null
) : Serializable