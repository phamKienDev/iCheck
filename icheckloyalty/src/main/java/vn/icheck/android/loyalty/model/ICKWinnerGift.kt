package vn.icheck.android.loyalty.model

import com.google.gson.annotations.Expose
import java.io.Serializable

class ICKWinnerGift : Serializable{
    @Expose
    var gift: ICKGift? = null

    @Expose
    val id: Long? = null

    @Expose
    val status: String? = null

    @Expose
    val status_title: String? = null

    @Expose
    val status_time: String? = null

    @Expose
    val status_time_title: String? = null

    @Expose
    val full_address: String? = null
}