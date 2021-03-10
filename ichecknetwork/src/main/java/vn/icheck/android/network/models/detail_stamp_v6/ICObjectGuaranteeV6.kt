package vn.icheck.android.network.models.detail_stamp_v6


import com.google.gson.annotations.Expose
import java.io.Serializable

data class ICObjectGuaranteeV6(
    @Expose
    var days: Int? = null,
    @Expose
    var expired_time: Long? = null,
    @Expose
    var status: Int? = null,
    @Expose
    var return_time: Long? = null,
    @Expose
    var note:String? = null

): Serializable