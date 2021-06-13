package vn.icheck.android.network.models.detail_stamp_v6_1

import com.google.gson.annotations.Expose

data class ICStampContact(
        @Expose
        var description: String? = null,
        @Expose
        var email: String? = null,
        @Expose
        var hotline: String? = null,
        @Expose
        var title: String? = null
)