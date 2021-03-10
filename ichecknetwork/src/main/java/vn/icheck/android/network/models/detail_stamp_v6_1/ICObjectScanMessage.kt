package vn.icheck.android.network.models.detail_stamp_v6_1

import com.google.gson.annotations.Expose
import java.io.Serializable

class ICObjectScanMessage : Serializable {

    @Expose
    var text: String? = null

    @Expose
    var sms_text: String? = null

    @Expose
    var is_success: Int? = null

    @Expose
    var redirect_warning: Boolean? = null
}