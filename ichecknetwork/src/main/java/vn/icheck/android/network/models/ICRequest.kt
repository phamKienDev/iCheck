package vn.icheck.android.network.models

import com.google.gson.annotations.Expose

data class ICRequest(
        @Expose val type: String? = null,
        @Expose val url: String? = null
)