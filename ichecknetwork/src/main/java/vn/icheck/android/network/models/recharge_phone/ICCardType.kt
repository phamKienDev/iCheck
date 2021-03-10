package vn.icheck.android.network.models.recharge_phone

import com.google.gson.annotations.Expose

data class ICCardType(
        @Expose
        var name: String? = null,
        @Expose
        var value: Long? = null
)