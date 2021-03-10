package vn.icheck.android.network.models.detail_stamp_v6


import com.google.gson.annotations.Expose
import java.io.Serializable

data class Image(
    @Expose
    var created_at: Int? = null,
    @Expose
    var item_id: Long? = null,
    @Expose
    var user_id: Long? = null,
    @Expose
    var url: String? = null,
    @Expose
    var alt: String? = null,
    @Expose
    var title: String? = null,
    @Expose
    var id: Long? = null,
    @Expose
    var description: String? = null
): Serializable