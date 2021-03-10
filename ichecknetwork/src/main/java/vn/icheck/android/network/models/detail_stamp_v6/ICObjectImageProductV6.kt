package vn.icheck.android.network.models.detail_stamp_v6


import com.google.gson.annotations.Expose
import java.io.Serializable

data class ICObjectImageProductV6(
    @Expose
    var alt: String? = null,
    @Expose
    var created_at: Long? = null,
    @Expose
    var description: Any? = null,
    @Expose
    var id: Int? = null,
    @Expose
    var item_id: Int? = null,
    @Expose
    var title: String? = null,
    @Expose
    var url: String? = null,
    @Expose
    var user_id: Int? = null
):Serializable