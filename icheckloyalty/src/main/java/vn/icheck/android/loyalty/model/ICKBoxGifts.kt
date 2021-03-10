package vn.icheck.android.loyalty.model

import com.google.gson.annotations.Expose
import java.io.Serializable

class ICKBoxGifts: Serializable {
    @Expose
    val id: Long? = null

    @Expose
    val gift_id: Long? = null

    @Expose
    val box_id: Long? = null

    @Expose
    val quota: Long? = null

    @Expose
    var points: Long? = null

    @Expose
    var total_gift: Long? = null

    @Expose
    val gift_used: Long? = null

    @Expose
    val total_code: Long? = null

    @Expose
    val chance: Long? = null

    @Expose
    val icoin: Long? = null

    @Expose
    val is_attached: Boolean? = null

    @Expose
    val message: String? = null

    @Expose
    var gift: ICKGift? = null

    @Expose
    var export_gift_to: String? = null

    @Expose
    var export_gift_from: String? = null

    /**
     * state = 1 -> Chờ xác nhận
     * state = 2 -> Chờ giao
     * state = 3 -> Đã nhận quà
     * state = 4 -> Từ chối
     * state = 5 -> Hủy
     */
    var state = 0
}