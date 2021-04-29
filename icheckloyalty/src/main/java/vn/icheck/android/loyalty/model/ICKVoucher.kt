package vn.icheck.android.loyalty.model

import com.google.gson.annotations.Expose
import java.io.Serializable

class ICKVoucher : Serializable {

    @Expose
    val can_use: Boolean? = null

    @Expose
    val code: String? = null

    @Expose
    val expired_at: String? = null

    @Expose
    val can_mark_use: Boolean? = null
}