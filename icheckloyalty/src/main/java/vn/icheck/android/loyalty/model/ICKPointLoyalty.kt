package vn.icheck.android.loyalty.model

import com.google.gson.annotations.Expose
import java.io.Serializable

class ICKPointLoyalty : Serializable {
    var type: Int = 0

    var listPoint: MutableList<ICKPointUser>? = null

    @Expose
    val point_name: String? = null

    @Expose
    val id: Long? = null

    @Expose
    val point_exchange: Long? = null

    @Expose
    val business_loyalty_id: Long? = null

    @Expose
    val businessLoyalty: ICKLongTermProgram? = null

    @Expose
    val gift: ICKGift? = null

    @Expose
    val quantity_remain: Long? = null

    @Expose
    val quantity: Int? = null
}