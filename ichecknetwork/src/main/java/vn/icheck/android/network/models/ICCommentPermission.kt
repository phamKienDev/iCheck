package vn.icheck.android.network.models

import com.google.gson.annotations.Expose

data class ICCommentPermission(
        @Expose val id: Long? = null,
        @Expose val avatar: String? = "",
        @Expose val name: String?,
        @Expose val type: String? = null,
)