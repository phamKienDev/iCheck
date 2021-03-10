package vn.icheck.android.network.models.detail_stamp_v6


import com.google.gson.annotations.Expose
import java.io.Serializable

data class ICObjectBusinessV6(
    @Expose
    var address: String? = null,
    @Expose
    var company_email: String? = null,
    @Expose
    var company_name: String? = null,
    @Expose
    var company_website: String? = null,
    @Expose
    var fullname: String? = null,
    @Expose
    var icheck_id: String? = null,
    @Expose
    var phone: String? = null
): Serializable