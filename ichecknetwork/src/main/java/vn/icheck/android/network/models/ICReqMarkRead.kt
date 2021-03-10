package vn.icheck.android.network.models

import com.google.gson.annotations.Expose

data class ICReqMarkRead(
        @Expose val id: Long? = null,
        @Expose val idList: MutableList<Long>? = null,
        @Expose val isAll: Boolean? = null
)