package vn.icheck.android.network.models.detail_stamp_v6


import com.google.gson.annotations.Expose
import java.io.Serializable

data class ICObjectServiceV6(
    @Expose
    var id: Int? = null,
    @Expose
    var code: String? = null,
    @Expose
    var name: String? = null,
    @Expose
    var message_success: String? = null,
    @Expose
    var message_warning: String? = null,
    @Expose
    var message_error: String? = null,
    @Expose
    var priority: Int? = null,
    @Expose
    var retail_price: Int? = null,
    @Expose
    var market_price: Int? = null,
    @Expose
    var sms_success: String? = null,
    @Expose
    var sms_error: String? = null,
    @Expose
    var show: Int? = null,
    @Expose
    var import_market_price: Int? = null,
    @Expose
    var import_retail_price: Int? = null
):Serializable