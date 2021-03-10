package vn.icheck.android.network.models

import com.google.gson.annotations.Expose

data class ICLayoutOtp(
        @Expose val content: String?,
        @Expose val timeout: Long?,
        @Expose val timechange: Long?,
        @Expose val type: String?
)