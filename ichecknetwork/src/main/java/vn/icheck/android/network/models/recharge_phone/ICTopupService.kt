package vn.icheck.android.network.models.recharge_phone


import com.google.gson.annotations.Expose
import java.io.Serializable

data class ICTopupService(
    @Expose
    var avatar: String? = null,
    @Expose
    var card_id: Long? = null,
    @Expose
    var code: String? = null,
    @Expose
    var denomination: MutableList<String>? = null,
    @Expose
    var id: Int? = null,
    @Expose
    var provider: String? = null,
    @Expose
    var topup_card: String? = null,
    @Expose
    var type: String? = null,
    @Expose
    var type_name: String? = null
): Serializable