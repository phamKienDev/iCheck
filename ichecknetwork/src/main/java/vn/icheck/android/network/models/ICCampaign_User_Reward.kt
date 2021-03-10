package vn.icheck.android.network.models

import com.google.gson.annotations.Expose

class ICCampaign_User_Reward {
    @Expose
    var name: String? = null
    @Expose
    var phone: String? = null
    @Expose
    var business_name: String? = null
    @Expose
    var reward_name: String? = null
    @Expose
    var receive_at: String? = null
    @Expose
    var avatar: String? = null
    @Expose
    var user_id: Long? = null
    @Expose
    var reward_image: String? = null
    @Expose
    var business_type: Int? = null

    // 1-vat pham , 2-eVoucher , 3-iCoin
    @Expose
    var type: Int? = null

    @Expose
    var icoin: Int? = null
}