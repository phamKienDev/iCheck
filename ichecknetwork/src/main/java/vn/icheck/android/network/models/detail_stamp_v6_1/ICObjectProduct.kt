package vn.icheck.android.network.models.detail_stamp_v6_1

import com.google.gson.annotations.Expose
import java.io.Serializable

data class ICObjectProduct(
        @Expose
        var approved_file: Int? = null,
        @Expose
        var attachments: MutableList<String>? = null,
        @Expose
        var created_time: String? = null,
        @Expose
        var deleted_time: String? = null,
        @Expose
        var id: Long? = null,
        @Expose
        var image: String? = null,
        @Expose
        var images: MutableList<String>? = null,
        @Expose
        var infos: MutableList<ICObjectInfo>? = null,
        @Expose
        var is_deleted: Int? = null,
        @Expose
        var name: String? = null,
        @Expose
        var number_batches: Int? = null,
        @Expose
        var number_stamps: Int? = null,
        @Expose
        var price: Long? = null,
        @Expose
        var sku: String? = null,
        @Expose
        var status: Int? = null,
        @Expose
        var updated_time: String? = null,
        @Expose
        var user_created: String? = null,
        @Expose
        var user_id: Long? = null,
        @Expose
        var vendor: ICObjectVendor? = null,
        @Expose
        var vendor_id: Long? = null,
        @Expose
        var video: String? = null,
        @Expose
        var barcode: String? = null
) : Serializable