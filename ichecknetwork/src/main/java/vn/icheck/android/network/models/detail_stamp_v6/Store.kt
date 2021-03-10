package vn.icheck.android.network.models.detail_stamp_v6


import com.google.gson.annotations.Expose
import java.io.Serializable

data class Store(
    @Expose
    var id: Int? = null,
    @Expose
    var name: String? = null,
    @Expose
    var type: Int? = null,
    @Expose
    var user_id: Int? = null,
    @Expose
    var status: Int? = null,
    @Expose
    var province: String? = null,
    @Expose
    var district: String? = null,
    @Expose
    var address: String? = null,
    @Expose
    var created_by: Int? = null,
    @Expose
    var created_at: Int? = null,
    @Expose
    var code: String? = null,
    @Expose
    var deleted: Int? = null,
    @Expose
    var deleted_time: Int? = null
): Serializable