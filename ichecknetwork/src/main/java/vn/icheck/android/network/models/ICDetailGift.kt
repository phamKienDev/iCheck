package vn.icheck.android.network.models

import com.google.gson.annotations.Expose
import java.io.Serializable

class ICDetailGift : Serializable {
    @Expose
    var image: String? = null
    @Expose
    var product_id: Long? = null
    @Expose
    var name: String? = null
    @Expose
    var shop_type: Int? = null
    @Expose
    var remain_time: String? = null
    @Expose
    var id: String? = null

    // 1-Chua nhan , 2-Da Ship , 3-Tu choi
    @Expose
    var state: Int? = null

    @Expose
    var rewardType: String? = null

    @Expose
    var expired_at: String? = null
    @Expose
    var shop_name: String? = null
    @Expose
    var shop_image: String? = null
    @Expose
    var order_id: Long? = null
    @Expose
    var desc: String? = null
}