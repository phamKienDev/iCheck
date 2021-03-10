package vn.icheck.android.network.models

import com.google.gson.annotations.Expose
import java.io.Serializable

class ICBoxReward : Serializable {

    @Expose
    var id: String? = null

    @Expose
    var title: String? = null

    @Expose
    var campaign_name: String? = null

    @Expose
    var campaign_id: String? = null

    @Expose
    var logo: String? = null

    @Expose
    var number: Long? = null

    @Expose
    var expire_at: String? = null

    // 1-chua dap , 2-da dap , 3-het han
    @Expose
    var state: Int? = null

    @Expose
    var business_name: String? = null

    // 1-iCheck Seller , 2-normal seller
    @Expose
    var business_type: Int? = null

    @Expose
    var remain_time: String? = null

}