package vn.icheck.android.network.models

import com.google.gson.annotations.Expose
import java.io.Serializable

data class ICMedia(
        @Expose var content: String? = null,
        @Expose var url: String? = null,
        @Expose var type: String? = null
) : Serializable