package vn.icheck.android.network.models.detail_stamp_v6


import com.google.gson.annotations.Expose
import java.io.Serializable

data class Guarantee(
    @Expose
    var days: Int? = null,
    @Expose
    var expired_time: Int? = null,
    @Expose
    var return_time: Int? = null,
    @Expose
    var status: Int? = null,
    @Expose
    var note: String? = null,
    @Expose
    var store_id: Int? = null
): Serializable