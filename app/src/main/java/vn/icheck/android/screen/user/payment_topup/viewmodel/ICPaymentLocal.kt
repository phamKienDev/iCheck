package vn.icheck.android.screen.user.payment_topup.viewmodel

import vn.icheck.android.network.model.ICNameValue
import vn.icheck.android.network.models.recharge_phone.ICRechargePhone

data class ICPaymentLocal (
        var value: String?,
//        var serviceId: Long?,
        var card: ICRechargePhone?,
        var listValue: MutableList<ICNameValue>?,
        var phoneNumber: String?,
        var type: Int?
)