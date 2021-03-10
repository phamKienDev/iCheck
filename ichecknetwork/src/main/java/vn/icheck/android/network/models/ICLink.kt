package vn.icheck.android.network.models

import com.google.gson.annotations.Expose

data class ICLink(
        @Expose val key: String? = null,
        @Expose val link: String? = null,
        @Expose val title: String? = null
)