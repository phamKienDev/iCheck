package vn.icheck.android.loyalty.model

import com.google.gson.annotations.Expose
import java.io.Serializable

class ICKWinner(
        @Expose
        var id: String? = null,
        @Expose
        var icheck_id: Long? = null,
        @Expose
        var customer_id: Long? = null,
        @Expose
        var campaign_id: Long? = null,
        @Expose
        var box_gift_id: Long? = null,
        @Expose
        var status: String? = null,
        @Expose
        var target_type: String? = null,
        @Expose
        var target: String? = null,
        @Expose
        var stamp: String? = null,
        @Expose
        var code: String? = null,
        @Expose
        var message: String? = null,
        @Expose
        var device_id: String? = null,
        @Expose
        var ip: String? = null,
        @Expose
        var geo: String? = null,
        @Expose
        var win_at: String? = null,
        @Expose
        var create_at: String? = null,
        @Expose
        var update_at: String? = null,
        @Expose
        var delete_at: String? = null

) : Serializable