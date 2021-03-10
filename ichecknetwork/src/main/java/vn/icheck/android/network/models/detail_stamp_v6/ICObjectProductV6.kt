package vn.icheck.android.network.models.detail_stamp_v6


import com.google.gson.annotations.Expose
import java.io.Serializable

data class ICObjectProductV6(
        @Expose
        var cate_id: Int? = null,
        @Expose
        var cate_id1: Int? = null,
        @Expose
        var cate_id2: Int? = null,
        @Expose
        var created_time: Long? = null,
        @Expose
        var deleted_time: Long? = null,
        @Expose
        var description: String? = null,
        @Expose
        var id: Long? = null,
        @Expose
        var image: String? = null,
        @Expose
        var images: MutableList<ICObjectImageProductV6>? = null,
        @Expose
        var is_deleted: Int? = null,
        @Expose
        var name: String? = null,
        @Expose
        var price: Long? = null,
        @Expose
        var serial_prefix: String? = null,
        @Expose
        var show_address: Int? = null,
        @Expose
        var sku: String? = null,
        @Expose
        var status: Int? = null,
        @Expose
        var type: Int? = null,
        @Expose
        var updated_time: Long? = null,
        @Expose
        var user_created: String? = null,
        @Expose
        var user_id: Int? = null,
        @Expose
        var vendor: String? = null,
        @Expose
        var vendor_id: Int? = null
) : Serializable