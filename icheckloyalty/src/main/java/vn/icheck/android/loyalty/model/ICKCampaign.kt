package vn.icheck.android.loyalty.model

import com.google.gson.annotations.Expose
import java.io.Serializable

class ICKCampaign : Serializable {
    @Expose
    var business_name: String? = null

    @Expose
    var business_type: Int? = null

    @Expose
    var logo: String? = null

    @Expose
    var success_number: Int = 0

    @Expose
    var id: String? = null

    @Expose
    var state: Int? = null

    @Expose
    var title: String? = null

    @Expose
    var ended_at: String? = null

    @Expose
    var begin_at: String? = null

    @Expose
    var created_at: String? = null

    @Expose
    var name: String? = null

    @Expose
    var avatar: ICThumbnail? = null

    @Expose
    var rankUser: String? = null

    @Expose
    var phone: String? = null

    @Expose
    var image: String? = null

    @Expose
    var winner_gifts: MutableList<ICKWinnerGift>? = null

    @Expose
    val export_gift_from: String? = null

    @Expose
    val export_gift_to: String? = null

}