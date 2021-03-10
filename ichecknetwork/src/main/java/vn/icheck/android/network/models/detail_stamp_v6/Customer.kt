package vn.icheck.android.network.models.detail_stamp_v6


import com.google.gson.annotations.Expose
import java.io.Serializable

data class Customer(
    @Expose
    var store_id: Int? = null,
    @Expose
    var district: String? = null,
    @Expose
    var city: String? = null,
    @Expose
    var name: String? = null,
    @Expose
    var phone: String? = null
): Serializable