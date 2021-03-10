package vn.icheck.android.network.models.detail_stamp_v6


import com.google.gson.annotations.Expose
import java.io.Serializable

data class RESP_Log_History_v6(
    @Expose
    var __v: Int? = null,
    @Expose
    var user_created: Int? = null,
    @Expose
    var user_id: Int? = null,
    @Expose
    var created_at: String? = null,
    @Expose
    var created_time: Int? = null,
    @Expose
    var product: Product? = null,
    @Expose
    var customer: Customer? = null,
    @Expose
    var returnTime: Int? = null,
    @Expose
    var type: Int? = null,
    @Expose
    var os: String? = null,
    @Expose
    var note: String? = null,
    @Expose
    var order: Order? = null,
    @Expose
    var order_id: Int? = null,
    @Expose
    var item_id: Int? = null,
    @Expose
    var code_sms: String? = null,
    @Expose
    var qrm: String? = null,
    @Expose
    var serial: String? = null,
    @Expose
    var code_id: String? = null,
    @Expose
    var stamp: Stamp? = null,
    @Expose
    var store_id: Int? = null,
    @Expose
    var id: String? = null,
    @Expose
    var user: User? = null,
    @Expose
    var store: Store? = null
) : Serializable