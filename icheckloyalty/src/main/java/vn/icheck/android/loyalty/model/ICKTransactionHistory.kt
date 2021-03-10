package vn.icheck.android.loyalty.model

import com.google.gson.annotations.Expose
import java.io.Serializable

class ICKTransactionHistory : Serializable {

    @Expose
    var type: ICKType? = null

    @Expose
    var reason: String? = null

    @Expose
    var created_at: String? = null

    @Expose
    var point: Long? = null
}