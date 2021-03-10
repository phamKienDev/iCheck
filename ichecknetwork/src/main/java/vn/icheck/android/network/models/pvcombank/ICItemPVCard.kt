package vn.icheck.android.network.models.pvcombank

import com.google.gson.annotations.Expose
import java.io.Serializable

data class ICItemPVCard(
        var used: Boolean? = null,
        var isLock: Boolean? = null,
        @Expose var cardId: String? = null,
        @Expose var cardMasking: String? = null,
        @Expose var embossName: String? = null,
        @Expose var cardStatus: String? = null,
        @Expose var avlBalance: String? = null,
        @Expose var exceedLimit: String? = null,
        @Expose var expDate: String? = null
) : Serializable