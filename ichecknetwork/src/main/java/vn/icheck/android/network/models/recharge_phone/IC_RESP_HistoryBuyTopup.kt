package vn.icheck.android.network.models.recharge_phone

import com.google.gson.annotations.Expose

data class IC_RESP_HistoryBuyTopup(
        @Expose
        var id: Long? = null,
        @Expose
        var networkOperator: ObjectTopupCard? = null,
        @Expose
        var cardType: ICCardType? = null,
        @Expose
        var seri:String? = null,
        @Expose
        var isUsed : Boolean? = null,
        @Expose
        var mobile:String? = null,
        @Expose
        var user_id: String? = null,
        @Expose
        var type: String? = null,
        @Expose
        var service_id: Int? = null,
        @Expose
        var topup_service: ICHistoryBuyTopupService? = null,
        @Expose
        var code: String? = null,
        @Expose
        var denomination: String? = null,
        @Expose
        var transaction_code: String? = null,
        @Expose
        var data: ICHistoryBuyTopupData? = null,
        @Expose
        var service_of_topup: String? = null,
        @Expose
        var service_of_topup_name: String? = null,
        @Expose
        var phone: String? = null,
        @Expose
        var createdAt: String? = null,
        @Expose
        var updated_at: String? = null
)