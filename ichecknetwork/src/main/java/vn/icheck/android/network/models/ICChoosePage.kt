package vn.icheck.android.network.models

import com.google.gson.annotations.Expose
import java.io.Serializable

data class ICChoosePage(
        @Expose var avatar: String? = null,
        @Expose var name: String? = null
) : Serializable