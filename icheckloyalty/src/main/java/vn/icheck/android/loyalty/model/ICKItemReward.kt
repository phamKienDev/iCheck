package vn.icheck.android.loyalty.model

import com.google.gson.annotations.Expose

class ICKItemReward {

    @Expose
    var image: ICThumbnail? = null

    @Expose
    var business_name: String? = null

    @Expose
    var number: Int? = null

    @Expose
    var business_type: Int? = null

    @Expose
    var receive_at: String? = null

    @Expose
    var remain_time: String? = null

    @Expose
    var id: String? = null

    @Expose
    var expired_at: String? = null

    @Expose
    var rewardType: String? = null

    @Expose
    var target: String? = null

    // 1-chua nhan , 2-da ship , 3-tu choi
    @Expose
    var state: Int? = null

    // 1-san pham , 2-voucher , 3-iCoin
    @Expose
    var type: Int? = null

    @Expose
    var title: String? = null

    @Expose
    var code: String? = null

    @Expose
    var created_at: String? = null

    @Expose
    var entity_id: Long? = null

    @Expose
    var bussiness_loyalty: Boolean? = null
}