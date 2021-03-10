package vn.icheck.android.network.models

import com.google.gson.annotations.Expose
import vn.icheck.android.network.models.recharge_phone.ICRechargePhone

class ICRechargeThePhone {
    @Expose
    var phoneCard: MutableList<ICRechargePhone>? = null
    @Expose
    var phoneTopup: MutableList<ICRechargePhone>? = null
}