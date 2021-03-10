package vn.icheck.android.network.models

import com.google.gson.annotations.Expose

data class ICThemeFunction(
        @Expose val label: String? = null,
        @Expose val width: Int? = null,
        @Expose val height: Int? = null,
        @Expose val scheme: String? = null,
        @Expose val source: String? = null,
        @Expose val label_color: String? = null,
        @Expose val required_login: Boolean? = null,
        @Expose val is_new: Boolean? = null,
        @Expose val featured: Boolean? = null,
        @Expose val is_hot: Boolean? = null,
        var background_source: Int = 0
)