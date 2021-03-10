package vn.icheck.android.network.models.detail_stamp_v6_1

import com.google.gson.annotations.Expose
import java.io.Serializable

data class ICObjectStoreGurantee(
        @Expose
        var id: Long? = null,
        @Expose
        var name: String? = null,
        @Expose
        var type: Int? = null,
        @Expose
        var user_id: Long? = null,
        @Expose
        var status: Int? = null,
        @Expose
        var province: Long? = null,
        @Expose
        var district: Long? = null,
        @Expose
        var address: String? = null,
        @Expose
        var created_by: Long? = null,
        @Expose
        var created_at: Long? = null,
        @Expose
        var code: String? = null,
        @Expose
        var deleted: Int? = null,
        @Expose
        var deleted_time: String? = null,
        @Expose
        var latitude: String? = null,
        @Expose
        var longitude: String? = null,
        @Expose
        var city_name: String? = null,
        @Expose
        var district_name: String? = null
): Serializable