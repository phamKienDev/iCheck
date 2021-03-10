package vn.icheck.android.network.models.detail_stamp_v6


import com.google.gson.annotations.Expose
import java.io.Serializable

data class Product(
    @Expose
    var images: MutableList<Image>? = null,
    @Expose
    var cate_id2: Long? = null,
    @Expose
    var cate_id1: Long? = null,
    @Expose
    var cate_id: Long? = null,
    @Expose
    var price: Int? = null,
    @Expose
    var vendor_id: Long? = null,
    @Expose
    var vendor: String? = null,
    @Expose
    var image: String? = null,
    @Expose
    var deleted_time: Int? = null,
    @Expose
    var is_deleted: Int? = null,
    @Expose
    var updated_time: Int? = null,
    @Expose
    var description: String? = null,
    @Expose
    var serial_prefix: String? = null,
    @Expose
    var show_address: Int? = null,
    @Expose
    var user_created: String? = null,
    @Expose
    var created_time: Int? = null,
    @Expose
    var type: Int? = null,
    @Expose
    var status: Int? = null,
    @Expose
    var name: String? = null,
    @Expose
    var user_id: Int? = null,
    @Expose
    var sku: String? = null,
    @Expose
    var id: Int? = null
): Serializable