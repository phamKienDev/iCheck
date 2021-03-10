package vn.icheck.android.network.models

import com.google.gson.annotations.Expose

data class ICKyc(
        @Expose val kycUrl: String?,
        @Expose val sessionId: String?,
        @Expose val redirectUrl: String?
)