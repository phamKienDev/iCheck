package vn.icheck.android.network.models

import com.google.gson.annotations.Expose

data class ICBanner(
        @Expose
        var id: Long? = null,
        @Expose
        var bannerId: String? = null,
        @Expose
        var bannerUrl: String? = null,
        @Expose
        var bannerSize: String? = null,
        @Expose
        var destinationUrl: String? = null
)