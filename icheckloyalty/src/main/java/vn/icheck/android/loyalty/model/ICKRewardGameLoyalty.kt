package vn.icheck.android.loyalty.model

import com.google.gson.annotations.Expose
import java.io.Serializable

class ICKRewardGameLoyalty : Serializable {

    @Expose
    var id: Long? = null

    @Expose
    var winner_id: Long? = null

    @Expose
    var gift_id: Long? = null

    @Expose
    var customer_icheck_id: Long? = null

    @Expose
    var campaign_id: Long? = null

    @Expose
    var state: Long? = null

    @Expose
    var points: Long? = null

    @Expose
    var winner: ICKWinnerGift? = null

    @Expose
    var created_at: String? = null

    @Expose
    var campaign: ICKCampaign? = null

    @Expose
    var gift: ICKGift? = null
}