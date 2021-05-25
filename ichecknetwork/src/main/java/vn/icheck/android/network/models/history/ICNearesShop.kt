package vn.icheck.android.network.models.history

import com.google.gson.annotations.Expose
import vn.icheck.android.network.models.ICShop
import java.io.Serializable

data class ICNearesShop(
        @Expose val id: Long? = null,
        @Expose val shop: ICShop? = null,
        @Expose val distance : Double? = null,
        var isClick : Boolean = false
) : Serializable
