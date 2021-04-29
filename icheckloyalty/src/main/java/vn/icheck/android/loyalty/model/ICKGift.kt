package vn.icheck.android.loyalty.model

import com.google.gson.annotations.Expose
import java.io.Serializable

class ICKGift : Serializable {
    @Expose
    val id: Long? = null

    @Expose
    val name: String? = null

    @Expose
    val image: ICThumbnail? = null

    @Expose
    val type: String? = null

    @Expose
    val description: String? = null

    @Expose
    val desc: String? = null

    @Expose
    val active: Boolean? = null

    @Expose
    val icoin: Long? = null

    @Expose
    val sponsor_type: Any? = null

    @Expose
    val owner: ICKOwner? = null

    @Expose
    val shop_image: String? = null

    @Expose
    val shop_name: String? = null

    @Expose
    val rewardType: String? = null

    @Expose
    val expired_at: String? = null

    @Expose
    var state: Int? = null // 1- chưa nhận , 2 -đã nhận, 3 - từ chối , 4- xác nhận ship

    @Expose
    val created_at: String? = null

    @Expose
    val voucher: ICKVoucher? = null
}