package vn.icheck.android.network.models

import com.google.gson.annotations.Expose
import java.io.Serializable

data class ICLocation(
        @Expose var lat: Double? = null,
        @Expose var lon: Double? = null,
        @Expose var lastName: String? = null
) : Serializable