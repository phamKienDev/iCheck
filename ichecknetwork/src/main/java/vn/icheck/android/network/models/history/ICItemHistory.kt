package vn.icheck.android.network.models.history

import com.google.gson.annotations.Expose
import vn.icheck.android.network.models.ICProduct
import java.io.Serializable

data class ICItemHistory(
        @Expose var actionType: String? = null,
        @Expose var product : ICProductScanHistory? = null,
        @Expose var numShopSell : Int? = null,
        @Expose var nearestShop : ICNearesShop? = null,
        @Expose var actionData: ICActionDataStampHistory? = null
) : Serializable
