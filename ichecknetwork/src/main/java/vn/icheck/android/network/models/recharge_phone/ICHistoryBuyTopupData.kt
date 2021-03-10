package vn.icheck.android.network.models.recharge_phone


import com.google.gson.annotations.Expose

data class ICHistoryBuyTopupData(
    @Expose
    var code: String? = null,
    @Expose
    var denomination: String? = null,
    @Expose
    var pin: String? = null,
    @Expose
    var serial: String? = null,
    @Expose
    var expiry_date: String? = null
)