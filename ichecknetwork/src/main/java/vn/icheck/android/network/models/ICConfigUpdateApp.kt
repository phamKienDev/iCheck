package vn.icheck.android.network.models

import com.google.gson.annotations.Expose

data class ICConfigUpdateApp(
        @Expose val id: Long = 0,
        @Expose val description: String? = null,
        @Expose val platform: String? = null,
        @Expose val isForced: Boolean? = null,
        @Expose val isSuggested: Boolean? = null,
        @Expose val createById: String? = null,
        @Expose val createdAt: String? = null,
        @Expose val latestCode: String? = null
)