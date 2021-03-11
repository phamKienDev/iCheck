package vn.icheck.android.network.models.pvcombank

import com.google.gson.annotations.Expose
import java.io.Serializable

data class ICListCardPVBank(
        @Expose var used: Boolean? = null,
        @Expose var isLock: Boolean? = null,
        @Expose var cardId: String? = null,
        @Expose var cardMasking: String? = null,
        @Expose var embossName: String? = null,
        @Expose var cardStatus: String? = null,
        @Expose var avlBalance: String? = "",
        @Expose var exceedLimit: String? = null,
        @Expose var expDate: String? = null,
        @Expose var isDefault: Boolean = false,
        var isShow : Boolean = true
) : Serializable