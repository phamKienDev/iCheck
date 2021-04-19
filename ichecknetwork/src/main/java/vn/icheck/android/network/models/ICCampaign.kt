package vn.icheck.android.network.models

import com.google.gson.annotations.Expose
import java.io.Serializable
import kotlin.Any

class ICCampaign : Serializable {
    @Expose
    var businessName: String? = null

    @Expose
    var business_type: Int? = null

    @Expose
    var logo: String? = null

    @Expose
    var successNumber: Int = 0

    @Expose
    var joinMember: Int = 0

    @Expose
    var id: String? = null

    @Expose
    var state: Any? = null    //0 :Chưa bắt đầu  1: Chưa tham gia 2: Đã tham gia 3:Đã hết hạn

    @Expose
    var title: String? = null

    @Expose
    var endedAt: String? = null

    @Expose
    var beginAt: String? = null

    @Expose
    var image: String? = null

    @Expose
    var imageProvider: String? = null

    @Expose
    var type: Int = 0

    @Expose
    var entityId: Int = 0

    @Expose
    var businessType: Int = 0

    @Expose
    var icoin: Long = 0

    @Expose
    var icoinIcon:String? = null

    @Expose
    var price: Long = 0

    @Expose
    var total: Long = 0

    @Expose
    var name: String? = null

    @Expose
    var phone: String? = null

    @Expose
    var rewardName: String? = null

    @Expose
    var receivedAt: String? = null

    @Expose
    var receiveAt: String? = null

    @Expose
    var avatar: String? = null

    @Expose
    var userId: Long = 0

    @Expose
    var rewardImage: String? = null

    @Expose
    var rankUser: String? = null

    @Expose
    var media: MutableList<ICMedia>? = null

    @Expose
    var daysLeft: Int? = 0

    @Expose
    var startTime: String? = null

    @Expose
    var itemCount: Int? = 0

    @Expose
    var available: Int? = null

    @Expose
    var valueReward: Long? = null

    @Expose
    var hasOnboarding: Boolean = false //false: vào màn onboard - true: vào màn gift
}