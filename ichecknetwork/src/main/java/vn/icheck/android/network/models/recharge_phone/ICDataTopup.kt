package vn.icheck.android.network.models.recharge_phone

import com.google.gson.annotations.Expose
import java.io.Serializable

data class ICDataTopup(
        @Expose
        var code: String? = null,
        @Expose
        var denomination: String? = null,
        @Expose
        var pin: String? = null,
        @Expose
        var serial: String? = null,
        @Expose
        var expiry_date: String? = null,
        @Expose
        var exprireDate: String? = null
): Serializable