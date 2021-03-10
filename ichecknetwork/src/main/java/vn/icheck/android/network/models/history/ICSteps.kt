package vn.icheck.android.network.models.history

import vn.icheck.android.network.models.ICLocation
import java.io.Serializable

data class ICSteps(
        var locations : MutableList<ICLocation>? = null
) : Serializable
