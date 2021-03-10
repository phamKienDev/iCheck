package vn.icheck.android.network.models.history

import com.google.gson.annotations.Expose
import vn.icheck.android.network.models.ICShop
import java.io.Serializable

data class ICNearesShop(
        @Expose val id: Int? = null,
        @Expose val shop: ICShop? = null,
        @Expose val distance : Long? = null,
        var isClick : Boolean = false
) : Serializable
