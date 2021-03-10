package vn.icheck.android.network.models

import com.google.gson.annotations.Expose
import java.io.Serializable


class ICReqUpdateUser : Serializable {
    @Expose
    var first_name: String? = null
    @Expose
    var last_name: String? = null
    @Expose
    var name: String? = null
    @Expose
    var phone: String? = null
    @Expose
    var email: String? = null
    @Expose
    var gender: String? = null
    @Expose
    var birth_day: Int? = null
    @Expose
    var birth_year: Int? = null
    @Expose
    var birth_month: Int? = null
    @Expose
    var avatar: String? = null
    @Expose
    var cover: String? = null
    @Expose
    var city_id: Int? = null
    @Expose
    var district_id: Int? = null
    @Expose
    var ward_id: Int? = null
    @Expose
    var country_id: Int? = null
    @Expose
    var address: String? = null
    @Expose
    var publish_fields: Array<String>? = null
}