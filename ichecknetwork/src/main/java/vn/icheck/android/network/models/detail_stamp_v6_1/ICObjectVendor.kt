package vn.icheck.android.network.models.detail_stamp_v6_1

import com.google.gson.annotations.Expose
import java.io.Serializable

data class ICObjectVendor(
    @Expose
    var address: String? = null,
    @Expose
    var city: String? = null,
    @Expose
    var city_id: Long? = null,
    @Expose
    var country_id: Long? = null,
    @Expose
    var country_name: String? = null,
    @Expose
    var created_time: Int? = null,
    @Expose
    var deleted_time: String? = null,
    @Expose
    var description: String? = null,
    @Expose
    var district: String? = null,
    @Expose
    var district_id: Long? = null,
    @Expose
    var email: String? = null,
    @Expose
    var ensign: String? = null,
    @Expose
    var gln_code: String? = null,
    @Expose
    var id: Long? = null,
    @Expose
    var info: String? = null,
    @Expose
    var is_deleted: Int? = null,
    @Expose
    var logo: String? = null,
    @Expose
    var name: String? = null,
    @Expose
    var phone: String? = null,
    @Expose
    var status: Int? = null,
    @Expose
    var updatedTime: String? = null,
    @Expose
    var user_created: String? = null,
    @Expose
    var user_id: Long? = null,
    @Expose
    var website: String? = null
): Serializable