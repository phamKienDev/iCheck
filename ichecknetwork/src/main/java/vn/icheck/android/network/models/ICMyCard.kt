package vn.icheck.android.network.models

import com.google.gson.annotations.Expose

data class ICMyCard(
        @Expose val banner: String? = null,
        @Expose val event_scheme: String? = null,
        @Expose val event_desc: String? = null
)