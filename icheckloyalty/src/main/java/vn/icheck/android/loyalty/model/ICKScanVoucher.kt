package vn.icheck.android.loyalty.model

import com.google.gson.annotations.Expose
import java.io.Serializable

class ICKScanVoucher : Serializable {

    @Expose
    var id: Long? = null

    @Expose
    val voucher: ICKVoucher? = null

    @Expose
    var rewardType: String? = null

    @Expose
    var shop_image: String? = null

    @Expose
    var shop_name: String? = null

    @Expose
    var image: ICThumbnail? = null

    @Expose
    var campaign_image: ICThumbnail? = null

    @Expose
    var name: String? = null

    @Expose
    var desc: String? = null
}