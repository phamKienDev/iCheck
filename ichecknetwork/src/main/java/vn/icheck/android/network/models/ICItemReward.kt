package vn.icheck.android.network.models

import com.google.gson.annotations.Expose
import java.io.Serializable

class ICItemReward : Serializable {

    @Expose
    var image: String? = null

    @Expose
    var business_name: String? = null

    @Expose
    var businessName: String? = null

    @Expose
    var number: Int? = null

    @Expose
    var businessLoyalty: Boolean? = null

    @Expose
    var business_type: Int? = null

    @Expose
    var receive_at: String? = null

    @Expose
    var entityId: Long? = null

    @Expose
    var businessId: Long? = null

    @Expose
    var value: Long? = null

    @Expose
    var remain_time: String? = null

    @Expose
    var remainTime: String? = null

    @Expose
    var id: String? = null

    @Expose
    var expired_at: String? = null

    @Expose
    var rewardType: String? = null

    @Expose
    var landingCode: String? = null

    // 1-chua nhan , 2-da ship , 3-tu choi, 4-dang ship
    @Expose
    var  state: Int? = null

    // 1-san pham , 2-voucher , 3-iCoin
    @Expose
    var type: Int? = null

    @Expose
    var code: String? = null

    @Expose
    var title: String? = null

    @Expose
    var entity_id: Long? = null

    @Expose
    var logo: String? = null

    @Expose
    var expiredAt: String? = null

    @Expose
    var receiveAt: String? = null

    @Expose
    var businessType: Any? = null

    @Expose
    var campaignName: String? = null

    @Expose
    var campaignId:String? = null

    @Expose
    var name:String? = null

    @Expose
    var productId:Long? = null

    @Expose
    var  shopName:String? = null

    @Expose
    var shopImage:String? = null

    @Expose
    var orderId:Long? = null

    @Expose
    var desc:String? = null

    @Expose
    var shopType:Int? = null

    @Expose
    var endedAt:String? = null

    @Expose
    var timeOrder:String? = null

    @Expose
    var refuse:String? = null

    @Expose
    var icoinIcon:String? = null


    var totalGifts = 0

}