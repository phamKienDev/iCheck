package vn.icheck.android.network.models.recharge_phone

import com.google.gson.annotations.Expose
import java.io.Serializable

data class ICRechargePhone(
        @Expose
        var id: Long? = null,
        @Expose
        var serviceId: Long? = null,
        @Expose
        var name: String? = null,
        @Expose
        var logo: String? = null,
        @Expose
        var type: String? = null,
        @Expose
        var type_name: String? = null,
        @Expose
        var serviceType: String? = null,
        @Expose
        var card_id: Int? = null,
        @Expose
        var topup_card: ObjectTopupCard? = null,
        @Expose
        var code: String? = null,
        @Expose
        var providers: String? = null,
        @Expose
        var provider: String? = null,
        @Expose
        var avatar: String? = null,
        @Expose
        var phone: String? = null,
        @Expose
        var vnpUrl: String? = null,
        @Expose
        var createdAt: String? = null,
        @Expose
        var isUsed: Boolean? = null,
        @Expose
        var accountBalance: Long? = null,
        @Expose
        var card: ICDataTopup? = null,
        @Expose
        var denomination: Any? = null,
        @Expose
        var agent: ICPayType? = null,
        @Expose
        var cardTypes: MutableList<ICCardType>? = null,
        var select: Boolean = false
) : Serializable

class ICPayType(
        @Expose
        var id: Long? = null,
        @Expose
        var name: String? = null,
        @Expose
        var code: String? = null,
        @Expose
        var avatar: String? = null
) : Serializable