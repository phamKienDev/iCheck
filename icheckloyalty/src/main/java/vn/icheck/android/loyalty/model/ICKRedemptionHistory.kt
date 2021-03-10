package vn.icheck.android.loyalty.model

import com.google.gson.annotations.Expose
import java.io.Serializable

class ICKRedemptionHistory : Serializable {

    @Expose
    var id: Long? = null

    @Expose
    var loyalty_gift: ICKPointLoyalty? = null

    @Expose
    var customer: ICKCustomer? = null

    @Expose
    var network: ICKNetwork? = null

    @Expose
    var business: ICKNetwork? = null

    @Expose
    var point_name: String? = null

    @Expose
    var message: String? = null

    @Expose
    var code: String? = null

    @Expose
    var businessLoyalty: ICKPointLoyalty? = null

    @Expose
    var owner: ICKOwner? = null

    @Expose
    var gift: ICKGift? = null

    @Expose
    var status: String? = null

    @Expose
    var win_at: String? = null
}