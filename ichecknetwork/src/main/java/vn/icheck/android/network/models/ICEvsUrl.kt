package vn.icheck.android.network.models

import com.google.gson.annotations.Expose

data class ICEvsUrl(
        @Expose val evs: MutableList<String>? = null,
        @Expose val trust_domain: String? = null,
        @Expose val icheck_qrcode: MutableList<String>? = null
)