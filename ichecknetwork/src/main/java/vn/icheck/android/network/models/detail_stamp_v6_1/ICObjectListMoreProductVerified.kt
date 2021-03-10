package vn.icheck.android.network.models.detail_stamp_v6_1

import com.google.gson.annotations.Expose

data class ICObjectListMoreProductVerified(
        @Expose
        var id: Long? = null,
        @Expose
        var image: String? = null,
        @Expose
        var is_deleted: Int? = null,
        @Expose
        var name: String? = null,
        @Expose
        var sku: String? = null,
        @Expose
        var status: Int? = null,
        var item_type: Int = 0
)