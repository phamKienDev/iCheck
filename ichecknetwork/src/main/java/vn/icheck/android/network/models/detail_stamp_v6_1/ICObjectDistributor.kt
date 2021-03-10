package vn.icheck.android.network.models.detail_stamp_v6_1

import com.google.gson.annotations.Expose
import java.io.Serializable

data class ICObjectDistributor(
        @Expose
        var address: String? = null,
        @Expose
        var attachments: MutableList<String>? = null,
        @Expose
        var avatar: String? = null,
        @Expose
        var city: String? = null,
        @Expose
        var city_id: Long? = null,
        @Expose
        var collaborator_phone: String? = null,
        @Expose
        var company_id: Long? = null,
        @Expose
        var created_at: String? = null,
        @Expose
        var deleted_at: String? = null,
        @Expose
        var district: String? = null,
        @Expose
        var district_id: Long? = null,
        @Expose
        var email: String? = null,
        @Expose
        var id: Long? = null,
        @Expose()
        var is_seller: Int? = null,
        @Expose
        var name: String? = null,
        @Expose
        var note: String? = null,
        @Expose
        var phone: String? = null,
        @Expose
        var seller_id: Long? = null,
        @Expose
        var seller_name: String? = null,
        @Expose
        var sms_support_address: Long? = null,
        @Expose
        var status: Int? = null,
        @Expose
        var type: Int? = null,
        @Expose
        var updated_at: String? = null,
        @Expose
        var username: String? = null,
        @Expose
        var website: String? = null
): Serializable