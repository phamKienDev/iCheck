package vn.icheck.android.network.models

import com.google.gson.annotations.Expose

data class ICAttributes(
        val id: Long? = null,
        @Expose
        var name: String? = null,
        @Expose
        var code: String? = null,
        @Expose
        var value: String? = null,
        var selected: Boolean = false
)