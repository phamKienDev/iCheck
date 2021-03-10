package vn.icheck.android.network.models.history

import com.google.gson.annotations.Expose
import java.io.Serializable

data class ICRoutesShop(
        @Expose var legs : MutableList<ICLegs>? = null
) : Serializable
