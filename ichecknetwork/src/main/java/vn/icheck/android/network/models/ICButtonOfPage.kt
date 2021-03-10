package vn.icheck.android.network.models

import com.google.gson.annotations.Expose
import java.io.Serializable

data class ICButtonOfPage(
        @Expose
        var buttonId: Long? = null,
        @Expose
        var buttonName: String? = null,
        @Expose
        var buttonCode: String? = null,
        @Expose
        var objectType: String? = null,
        @Expose
        var status: Int? = null
) : Serializable