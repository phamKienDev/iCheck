package vn.icheck.android.loyalty.model

import com.google.gson.annotations.Expose
import java.io.Serializable

class ICKReceiveGift : Serializable {

    @Expose
    val message: String? = null

    @Expose
    val gift: ICKGift? = null

    @Expose
    var winner: ICKWinner? = null

    @Expose
    var gifts: MutableList<ICKGift>? = null
}