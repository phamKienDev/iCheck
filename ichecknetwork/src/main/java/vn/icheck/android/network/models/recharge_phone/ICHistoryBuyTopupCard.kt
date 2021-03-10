package vn.icheck.android.network.models.recharge_phone


import com.google.gson.annotations.Expose

data class ICHistoryBuyTopupCard(
    @Expose
    var id: Int? = null,
    @Expose
    var code: String? = null,
    @Expose
    var name: String? = null
)