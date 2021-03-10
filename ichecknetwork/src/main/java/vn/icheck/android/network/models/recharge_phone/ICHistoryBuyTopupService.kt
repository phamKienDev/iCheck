package vn.icheck.android.network.models.recharge_phone


import com.google.gson.annotations.Expose

data class ICHistoryBuyTopupService(
        @Expose
        var id: Int? = null,
        @Expose
        var type: String? = null,
        @Expose
        var type_name: String? = null,
        @Expose
        var card_id: Int? = null,
        @Expose
        var topup_card: ICHistoryBuyTopupCard? = null,
        @Expose
        var code: String? = null,
        @Expose
        var provider: String? = null,
        @Expose
        var avatar: String? = null,
        @Expose
        var denomination: MutableList<String>? = null
)