package vn.icheck.android.loyalty.model

import com.google.gson.annotations.Expose
import java.io.Serializable

class ICKBox : Serializable {
    @Expose
    val id: Long? = null

    @Expose
    val points: Long? = null

    @Expose
    val gift_used: Long? = null

    @Expose
    val total_code: Long? = null

    @Expose
    val campaign_id: Long? = null

    @Expose
    val type: String? = null

    @Expose
    val box_gifts: MutableList<ICKBoxGifts>? = null

    @Expose
    val gifts: MutableList<ICKGift>? = null
}