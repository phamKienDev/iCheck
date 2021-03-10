package vn.icheck.android.network.models.detail_stamp_v6_1

import com.google.gson.annotations.Expose
import java.io.Serializable

data class ICObjectInfo(
    @Expose
    var created_at: String? = null,
    @Expose
    var id: Long? = null,
    @Expose
    var product_id: Long? = null,
    @Expose
    var short_content: String? = null,
    @Expose
    var title: String? = null,
    @Expose
    var updated_at: String? = null,
    @Expose
    var user_created: Long? = null,
    @Expose
    val image: String? = null
): Serializable