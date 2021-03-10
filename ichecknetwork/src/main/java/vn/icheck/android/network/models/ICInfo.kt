package vn.icheck.android.network.models

import com.google.gson.annotations.Expose

data class ICInfo(
        @Expose val icon: String? = null,
        @Expose val link: String? = null,
        @Expose val title: String? = null,
        @Expose val icon_url: String? = null
)