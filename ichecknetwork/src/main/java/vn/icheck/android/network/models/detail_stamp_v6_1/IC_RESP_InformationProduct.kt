package vn.icheck.android.network.models.detail_stamp_v6_1

import com.google.gson.annotations.Expose

class IC_RESP_InformationProduct {
    @Expose
    var error: Boolean? = null
    @Expose
    var status: Int? = null
    @Expose
    var data: Data? = null

    data class Data(
            @Expose
            var id: Int? = null,
            @Expose
            var userCreated: Int? = null,
            @Expose
            var productId: Int? = null,
            @Expose
            var title: String? = null,
            @Expose
            var content: String? = null,
            @Expose
            var createdAt: String? = null,
            @Expose
            var updatedAt: String? = null
    )
}