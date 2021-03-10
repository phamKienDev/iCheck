package vn.icheck.android.network.models.recharge_phone

import com.google.gson.annotations.Expose
import java.io.Serializable

data class IC_RESP_Buy_Recharge_Phone(
        @Expose
        var statusCode:Int? = null,
        @Expose
        var message:String? = null,
        @Expose
        var code: String? = null,
        @Expose
        var createdAt: String? = null,
        @Expose
        var data: ICDataTopup? = null,
        @Expose
        var denomination: String? = null,
        @Expose
        var id: String? = null,
        @Expose
        var phone: String? = null,
        @Expose
        var service_id: Int? = null,
        @Expose
        var service_of_topup: String? = null,
        @Expose
        var service_of_topup_name: String? = null,
        @Expose
        var topup_service: ICTopupService? = null,
        @Expose
        var transaction_code: String? = null,
        @Expose
        var type: String? = null,
        @Expose
        var updatedAt: String? = null,
        @Expose
        var used: Boolean? = null,
        @Expose
        var user_id: String? = null
):Serializable