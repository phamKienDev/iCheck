package vn.icheck.android.network.models

import com.google.gson.annotations.Expose
import java.io.Serializable

data class ICDistance(
        @Expose val unit: String = "",
        @Expose val value: Double = 0.0
) : Serializable