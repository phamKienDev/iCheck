package vn.icheck.android.loyalty.model

import com.google.gson.annotations.Expose
import java.io.Serializable

class ICKScanVoucher : Serializable {

    @Expose
    var id: Long? = null

    @Expose
    val voucher: ICKVoucher? = null

    @Expose
    var businessLoyalty: ICKPointLoyalty? = null

    @Expose
    var gift: ICKGift? = null

    @Expose
    var status: String? = null

    @Expose
    var owner: ICKOwner? = null

    @Expose
    var status_title: String? = null
}