package vn.icheck.android.network.models.history

import com.google.gson.annotations.Expose
import java.io.Serializable

data class ICLegs(
        @Expose var steps : MutableList<ICSteps>? = null
) : Serializable
