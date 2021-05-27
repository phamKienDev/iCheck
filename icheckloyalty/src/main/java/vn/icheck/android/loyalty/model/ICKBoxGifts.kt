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
    val box_gift: ICKBox? = null

    @Expose
    val quota: Long? = null

    @Expose
    var points: Long? = null

    @Expose
    var status: String? = null

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
    var voucher: ICKVoucher? = null

    @Expose
    var export_gift_to: String? = null

    @Expose
    var export_gift_from: String? = null


    @Expose
    var owner: ICKOwner? = null

    /**
     * state = 1 -> Chờ xác nhận
     * state = 2 -> Chờ giao
     * state = 3 -> Đã nhận quà
     * state = 4 -> Từ chối
     * state = 5 -> Hủy
     */
    var state = 0

    /**
     * sau khi đã sửa theo điều kiện
     * title Date
     * date
     * status
     * color Text status
     * color Background status
     */
    var titleDate: String? = null

    var dateChange: String? = null

    var statusChange: String? = null

    var colorText: Int = 0

    var colorBackground: Int = 0
    /**
     * End
     */
}