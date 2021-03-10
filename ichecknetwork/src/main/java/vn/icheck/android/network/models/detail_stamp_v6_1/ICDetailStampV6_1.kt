package vn.icheck.android.network.models.detail_stamp_v6_1

import com.google.gson.annotations.Expose
import java.io.Serializable

class ICDetailStampV6_1 : Serializable {
    @Expose
    var error: Boolean? = null

    @Expose
    var status: Int? = null

    @Expose
    var data: ICObjectDetailStamp? = null

    class ICObjectDetailStamp : Serializable {
        @Expose
        var message: ICMessageErrorStamp? = null

        @Expose
        var error_code: String? = null

        @Expose
        var active_require_profile: Int? = null

        @Expose
        var guarantee: ICObjectGuarantee? = null

        @Expose
        var distributor: ICObjectDistributor? = null

        @Expose
        var product: ICObjectProduct? = null

        @Expose
        var current_scanner: ICObjectCurrentScanner? = null

        @Expose
        var scan_message: ICObjectScanMessage? = null

        @Expose
        var show_analytic: Int? = null

        @Expose
        var count: ICObjectCount? = null

        @Expose
        var show_distributor: Int? = null

        @Expose
        var show_vendor: Int? = null

        @Expose
        var force_update: Boolean? = null

        @Expose
        var can_update: Boolean? = null

        @Expose
        var seller_id: Long? = null

        @Expose
        var barcode: String? = null
    }
}