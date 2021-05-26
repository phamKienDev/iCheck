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
    val start_at: String? = null

    @Expose
    val end_at: String? = null

    @Expose
    val released_at: String? = null

    @Expose
    val effective_time: String? = null

    @Expose
    val effective_type: String? = null

    @Expose
    val can_mark_use: Boolean? = null

    @Expose
    var checked_condition: ICKCheckedCondition? = null
}