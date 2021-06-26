package vn.icheck.android.network.models

import com.google.gson.annotations.Expose
import java.io.Serializable

class ICLoyaltyApp : Serializable {

    @Expose
    val campaignId: String? = null

    @Expose
    val campaignCode: String? = null

    @Expose
    val giftCode: String? = null

    @Expose
    val type: Int? = null
}