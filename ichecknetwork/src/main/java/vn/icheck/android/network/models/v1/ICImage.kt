package vn.icheck.android.network.models.v1

import com.google.gson.annotations.Expose

data class ICImage(
        @Expose var url: String?,
        @Expose var type: String? = null
)
