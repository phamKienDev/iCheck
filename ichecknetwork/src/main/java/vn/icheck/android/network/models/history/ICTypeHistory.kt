package vn.icheck.android.network.models.history

import com.google.gson.annotations.Expose
import java.io.Serializable

data class ICTypeHistory(
        @Expose var type: String? = null,
        @Expose var name: String? = null,
        var idShop:Long? = null,
        var select: Boolean = false
) : Serializable
